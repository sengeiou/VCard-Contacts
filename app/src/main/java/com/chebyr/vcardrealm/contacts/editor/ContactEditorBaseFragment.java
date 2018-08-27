package com.chebyr.vcardrealm.contacts.editor;

public class ContactEditorBaseFragment
{
    /**
     * Intent key to pass a Bundle of raw contact IDs to photos URIs between the compact editor
     * and the fully expanded one.
     */
    public static final String INTENT_EXTRA_UPDATED_PHOTOS = "updated_photos";

    /**
     * Intent extra to specify whether the save was initiated as a result of a back button press
     * or because the framework stopped the editor Activity.
     */
    public static final String INTENT_EXTRA_SAVE_BACK_PRESSED = "saveBackPressed";

    public static final String INTENT_EXTRA_DISABLE_DELETE_MENU_OPTION =
            "disableDeleteMenuOption";

    public static final String INTENT_EXTRA_NEW_LOCAL_PROFILE = "newLocalProfile";

    /**
     * Intent key to pass the photo palette primary color calculated by
     * {@link //QuickContactActivity} to the editor and between
     * the compact and fully expanded editors.
     */
    public static final String INTENT_EXTRA_MATERIAL_PALETTE_PRIMARY_COLOR =
            "material_palette_primary_color";

    /**
     * Intent key to pass the photo palette secondary color calculated by
     * {@link //QuickContactActivity} to the editor and between
     * the compact and fully expanded editors.
     */
    public static final String INTENT_EXTRA_MATERIAL_PALETTE_SECONDARY_COLOR =
            "material_palette_secondary_color";

    /**
     * Intent key to pass the ID of the photo to display on the editor.
     */
    public static final String INTENT_EXTRA_PHOTO_ID = "photo_id";

    /**
     * Intent key to pass the ID of the name to display on the editor.
     */
    public static final String INTENT_EXTRA_NAME_ID = "name_id";

}
