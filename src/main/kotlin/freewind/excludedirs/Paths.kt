package freewind.excludedirs

import org.apache.commons.lang.StringUtils

fun isSubPath(path: String, parent: String): Boolean {
    val p1 = standardize(path)
    val p2 = standardize(parent)
    return p1 == p2 || p1.startsWith("$p2/")
}

fun standardize(path: String) = StringUtils.stripEnd(path, "./")

fun relativePath(base: String, fullPath: String): String {
    val relative = StringUtils.removeStart(fullPath, base)
    return if (relative.isEmpty()) "/" else relative
}