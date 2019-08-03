package freewind.excludedirs

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.messages.MessageBusConnection
import org.apache.commons.io.IOUtils

fun readFileContent(file: VirtualFile): String? {
    return IOUtils.toString(file.inputStream, file.charset)
}

fun getRootModule(currentProject: Project): ModifiableRootModel? {
    val modules = ModuleManager.getInstance(currentProject).modules
    println("ModuleManager.getInstance(currentProject).modules: ${modules.size}")
    val module = modules[0]
    println("module: $module")
    return if (module != null) {
        val rootModel = ModuleRootManager.getInstance(module).modifiableModel
        rootModel
    } else {
        null
    }
}

fun getContentEntry(rootModel: ModifiableRootModel): ContentEntry? {
    return rootModel.contentEntries[0]
}

class MyProjectComponent(private val currentProject: Project) : ProjectComponent {

    var conn: MessageBusConnection? = null

    private fun detectChanges(baseDir: VirtualFile) {
        println("> detectChanges")
        conn = ApplicationManager.getApplication().messageBus.connect()
        conn?.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun before(events: MutableList<out VFileEvent>) {
                println("> before: ${events.joinToString(",")}")
            }

            override fun after(events: MutableList<out VFileEvent>) {
                println("> after: ${events.joinToString(",")}")


                // .exclude file
                val excludeEvents = events.any { event ->
                    val relativeFilePath = relativePath(currentProject.baseDir.path, event.file!!.path)
                    println("relativeFilePath: $relativeFilePath")
                    relativeFilePath == "/.exclude"
                }
                println("excludeEvents: $excludeEvents")

                // dir creation/rename/move
                val dirEvents = events.filter { event -> event.file!!.isDirectory }
                println("dirEvents: ${dirEvents.joinToString(",")}")

                if (excludeEvents || dirEvents.isNotEmpty()) {
                    println("need to handle")
                    val rules = readRules(baseDir)
                    if (rules.isEmpty()) {
                        return
                    }
                    if (excludeEvents) {
                        reloadExcludeFile(baseDir, rules)
                    } else {
                        val rootModule = getRootModule(currentProject)!!
                        println("rootModule: $rootModule")

                        val entry = getContentEntry(rootModule)!!
                        println("entry: $entry")
                        dirEvents.forEach { event ->
                            handleDir(baseDir, entry, rules, event.file!!)
                        }
                        commitChanges(rootModule)
                    }
                }
            }
        })
    }

    override fun disposeComponent() {
        println("> disposeComponent")
        conn?.disconnect()
    }

    override fun projectOpened() {
        println("> projectOpened: $currentProject")

        val baseDir = currentProject.baseDir

        val rules = readRules(baseDir)
        if (rules.isNotEmpty()) {
            reloadExcludeFile(baseDir, rules)
        }

        detectChanges(baseDir)
    }

    private fun readRules(baseDir: VirtualFile): List<Rule> {
        val excludeFile = baseDir.findChild(".exclude")
        println("excludeFile: $excludeFile")

        if (excludeFile != null) {
            val content = readFileContent(excludeFile)
            if (content != null) {
                return parseRules(content)
            }
        }
        return emptyList()
    }

    private fun reloadExcludeFile(baseDir: VirtualFile, rules: List<Rule>) {
        val rootModule = getRootModule(currentProject)!!
        println("rootModule: $rootModule")

        val entry = getContentEntry(rootModule)!!
        println("entry: $entry")

        println("> reloadExcludeFile")
        VfsUtilCore.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<VirtualFile>() {
            override fun visitFile(file: VirtualFile): Boolean {
                println("visitFile: $file")
                return if (file.isDirectory) {
                    when (handleDir(baseDir, entry, rules, file)) {
                        HandleDirResult.Excluded -> false
                        else -> true
                    }
                } else {
                    false
                }
            }
        })
        this.commitChanges(rootModule)
    }

    private fun commitChanges(rootModule: ModifiableRootModel) {
        println("> commitChanges")
        ApplicationManager.getApplication().runWriteAction {
            println("commit changes")
            rootModule.commit()
        }
    }


    private fun handleDir(baseDir: VirtualFile, entry: ContentEntry, rules: List<Rule>, file: VirtualFile): HandleDirResult {
        val relativeFilePath = relativePath(baseDir.path, file.path)
        println("> handleDir: $relativeFilePath")

        if (rules.any { it.matches(relativeFilePath) }) {
            println("Need to exclude: $file")
            // need to exclude
            entry.addExcludeFolder(file)
            return HandleDirResult.Excluded
        } else {
            val existing = entry.excludeFolders.find { it.file == file }
            if (existing != null) {
                println("Cancel exclude: $file")
                entry.removeExcludeFolder(existing)
                return HandleDirResult.Canceled
            }
            return HandleDirResult.NoOp
        }
    }

}

enum class HandleDirResult {
    Excluded, Canceled, NoOp
}