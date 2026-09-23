package me.akainth.ambient.snarf

import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel

/**
 * Type-safe representation of a snarf site
 *
 * @author akainth
 */
class SnarfSite constructor(snarfSiteDocument: Document) {

    private val packages: Array<out Package>
    val treeModel: TreeModel

    init {
        val root: DefaultMutableTreeNode

        val snarfSites = snarfSiteDocument.getElementsByTagName("snarf_site")

        if (snarfSites.length > 0) {
            // Existing Snarf format
            val snarfSiteElement = snarfSites.item(0) as Element

            val packageNodes = snarfSiteElement.getElementsByTagName("package")

            packages = Array(packageNodes.length) {
                Package(packageNodes.item(it) as Element)
            }

            root = DefaultMutableTreeNode(
                snarfSiteElement.getAttribute("name")
            )

            packages
                .groupBy { it.category }
                .forEach { (category, categoryPackages) ->
                    val categoryTreeNode = DefaultMutableTreeNode(category)

                    categoryPackages.forEach {
                        categoryTreeNode.add(it.treeNode)
                    }

                    root.add(categoryTreeNode)
                }

        } else {
            // Virginia Tech project-imports format
            val projectImports =
                snarfSiteDocument.getElementsByTagName("project-imports")
                    .item(0) as? Element
                    ?: throw IllegalArgumentException(
                        "Document is neither a Snarf site nor a project-imports document"
                    )

            val projectGroups =
                projectImports.getElementsByTagName("project-group")

            val packageList = mutableListOf<Package>()

            for (i in 0 until projectGroups.length) {
                val group = projectGroups.item(i) as Element
                val category = group.getAttribute("name")

                val projects = group.getElementsByTagName("project")

                for (j in 0 until projects.length) {
                    val project = projects.item(j) as Element

                    packageList.add(
                        Package(
                            name = project.getAttribute("name"),
                            category = category,
                            entry = project.getAttribute("uri")
                        )
                    )
                }
            }

            packages = packageList.toTypedArray()

            root = DefaultMutableTreeNode("Projects")

            packages
                .groupBy { it.category }
                .forEach { (category, categoryPackages) ->
                    val categoryTreeNode = DefaultMutableTreeNode(category)

                    categoryPackages.forEach {
                        categoryTreeNode.add(it.treeNode)
                    }

                    root.add(categoryTreeNode)
                }
        }

        treeModel = DefaultTreeModel(root)
    }
}

}
