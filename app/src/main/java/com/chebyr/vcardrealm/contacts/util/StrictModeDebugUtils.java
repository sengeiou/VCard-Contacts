/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chebyr.vcardrealm.contacts.html.utils;

import android.annotation.TargetApi;
import android.os.StrictMode;

/**
 * This class contains static utility methods.
 */
public class StrictModeDebugUtils
{

    // Prevents instantiation.
    private StrictModeDebugUtils() {}

    /**
     * Enables strict mode. This should only be called when debugging the application and is useful
     * for finding some potential bugs or best practice violations.
     */
    @TargetApi(11)
    public static void enableStrictMode()
    {
        // Enable all thread strict mode policies
        StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder();

        threadPolicyBuilder.detectAll(); // Detect everything that's potentially suspect
        threadPolicyBuilder.penaltyDeath(); // Crash the whole process on violation
        threadPolicyBuilder.penaltyLog(); // Log detected violations to the system log
        threadPolicyBuilder.penaltyFlashScreen(); // Flash screen when thread policy is violated
        threadPolicyBuilder.penaltyDeathOnNetwork(); // Crash the whole process on any network usage.

        StrictMode.ThreadPolicy threadPolicy = threadPolicyBuilder.build();
        StrictMode.setThreadPolicy(threadPolicy); // Use builders to enable strict mode policies

        // Enable all VM strict mode policies
        StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder();
        vmPolicyBuilder.detectAll(); // Detect everything that's potentially suspect
        vmPolicyBuilder.penaltyLog();
        // vmPolicyBuilder.setClassInstanceLimit(ContactDetailActivity.class, 1);
        StrictMode.VmPolicy vmPolicy = vmPolicyBuilder.build();
        StrictMode.setVmPolicy(vmPolicy);

    }

}
