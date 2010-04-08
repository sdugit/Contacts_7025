/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.contacts.mvcframework;

import android.database.ContentObserver;
import android.os.Handler;

public abstract class Loader<E> {
    protected E mCallbacks;

    protected final class ForceLoadContentObserver extends ContentObserver {
        public ForceLoadContentObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            forceLoad();
        }
    }

    /**
     * Registers a class that will receive callbacks when a load is complete. The callbacks will
     * be called on the UI thread so it's safe to pass the results to widgets.
     *
     * Must be called from the UI thread
     */
    public void registerCallbacks(E callbacks) {
        if (mCallbacks != null) {
            throw new IllegalStateException("There are already callbacks registered");
        }
        mCallbacks = callbacks;
    }

    /**
     * Must be called from the UI thread
     */
    public void unregisterCallbacks(E callbacks) {
        if (mCallbacks == null) {
            throw new IllegalStateException("No callbacks register");
        }
        if (mCallbacks != callbacks) {
            throw new IllegalArgumentException("Attempting to unregister the wrong callbacks");
        }
        mCallbacks = null;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately. The loader will monitor the source of
     * the data set and may deliver future callbacks if the source changes. Calling
     * {@link #stopLoading} will stop the delivery of callbacks.
     *
     * Must be called from the UI thread
     */
    public abstract void startLoading();

    /**
     * Force an asynchronous load. Unlike {@link #startLoading()} this will ignore a previously
     * loaded data set and load a new one.
     */
    public abstract void forceLoad();

    /**
     * Stops delivery of updates.
     */
    public abstract void stopLoading();

    /**
     * Must be called from the UI thread
     */
    public abstract void destroy();
}