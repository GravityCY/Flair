package me.gravityio.flair;

public class FlairMixinData {
    /**
     * A BACKUP, SINCE SOME MODS LIKE HODGEPODGE CANCEL THE CLICK EVENTS IN Container#clickSlot (since they wrap the whole method and control when to call it)
     * SO THE SECOND MOST CONSISTENT OPTION IS TO ALSO CHECK WHEN THE CLIENT IS ABOUT TO TELL THE CONTAINER THAT IT WAS CLICKED at PlayerControllerMP#windowClick
     *
     * THIS VARIABLE ENSURES THAT ONLY ONE EVENT IS FIRE, EITHER THE WINDOW CLICK, OR THE ACTUAL SLOT CLICK
     */
    public static boolean FROM_WINDOW_CLICK = false;
}
