package app.revanced.patches.spotify.navbar

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patches.shared.misc.mapping.get
import app.revanced.patches.shared.misc.mapping.resourceMappingPatch
import app.revanced.patches.shared.misc.mapping.resourceMappings

internal var showBottomNavigationItemsTextId = -1L
    private set
internal var createTabId = -1L
    private set

private val createNavbarTabResourcePatch = resourcePatch {
    dependsOn(resourceMappingPatch)

    execute {
        createTabId = resourceMappings["id", "create_tab"]

        showBottomNavigationItemsTextId = resourceMappings[
            "bool",
            "show_bottom_navigation_items_text",
        ]
    }
}

@Suppress("unused")
val createNavbarTabPatch = bytecodePatch(
    name = "Create navbar tab",
    description = "Hides the create tab from the navigation bar.",
) {
    dependsOn(createNavbarTabResourcePatch)

    compatibleWith("com.spotify.music")

    // If the navigation bar item is the create tab, do not add it.
    execute {
        addNavBarItemFingerprint.method.addInstructions(
            0,
            """
                const v1, $createTabId
                if-ne p5, v1, :continue
                return-void
                :continue
                nop
            """,
        )
    }
}
