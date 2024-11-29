package com.example.b07demosummer2024;

import java.util.HashMap;
import java.util.Map;

/*
This class is used for the sole purpose in determining if we should show the pop up message for the user
to start the primary data collection.
 */
public class DialogState {
    public static Map<String, Boolean> dialogState = new HashMap<>();

    public static boolean dialogShown(String uId) {
        Boolean state = dialogState.get(uId);
        return state != null && state;
    }

    public static void setDialogState(String uId, boolean b) {
        dialogState.put(uId, b);
    }

    public static void  clearState(String uId) {
        dialogState.remove(uId);
    }
}
