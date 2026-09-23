package me.akainth.ambient.snarf

import org.w3c.dom.Element
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Describes a package in a Snarf site
 *
 * @author akainth
 */
@Suppress("unused")
class Package(
    val name: String,
    val category: String,
    val publisher: String = "",
    val version: String = "",
    val projectType: String = "Java",
    val description: String = "",
    val entry: String
) {
        constructor(source: Element) : this(
            name = source.getAttribute("name"),
            category = source.getAttribute("category"),
            publisher = source.getAttribute("publisher"),
            version = source.getAttribute("version"),
            projectType = source.getAttribute("project_type"),
            description = source.getElementsByTagName("description")
                .item(0)?.textContent ?: "",
            entry = (source.getElementsByTagName("entry")
                .item(0) as Element).getAttribute("url")
        )

        val treeNode
            get() = DefaultMutableTreeNode(this)

        override fun toString(): String = name
    }
}
