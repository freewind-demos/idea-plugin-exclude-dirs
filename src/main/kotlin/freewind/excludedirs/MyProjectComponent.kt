package freewind.excludedirs

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModel
import com.intellij.openapi.vfs.VFileProperty
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import java.io.File

import com.intellij.openapi.roots.ContentEntry

class MyProjectComponent(val currentProject: Project) : ProjectComponent {
    override fun initComponent() {
        println("> initComponent: $currentProject")
        val excludeFile = currentProject.baseDir.path + "/.exclude"
        println("excludeFile: ${excludeFile}")
        File(excludeFile).readLines()
                .map { it.trim() }
                .filter { it.isEmpty() }
                .filter { !it.startsWith("#") }
                .forEach { }
        val x: ContentEntry;
//        ModuleRootManager.getInstance(currentProject.mo)
//        ModuleRootManager.getInstance(currentProject.mo)

        val modules = ModuleManager.getInstance(currentProject).modules

        modules.forEach { module ->
            VfsUtilCore.visitChildrenRecursively(currentProject.baseDir, object : VirtualFileVisitor() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (file.isDirectory && file.`is`(VFileProperty.SYMLINK)) {
                        module.addExcludeFolder(file)
                        return false
                    } else {
                        return true
                    }
                }
            })

        }



    }

    override fun projectOpened() {
        println("> projectOpened: $currentProject")
    }
}