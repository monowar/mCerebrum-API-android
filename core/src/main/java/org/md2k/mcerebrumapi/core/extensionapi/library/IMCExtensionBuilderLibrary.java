package org.md2k.mcerebrumapi.core.extensionapi.library;

import android.app.Activity;
import android.graphics.Bitmap;


/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
interface IMCExtensionBuilderLibrary {
    interface IId {
        IIName setId(String id);
    }

    interface IIName {
        IIDescription setName(String name);
    }
    interface IIDescription {
        IIIcon setDescription(String description);
    }

    interface IIIcon {
        IIVersion setIcon(Bitmap icon);
    }

    interface IIVersion {
        IIPermission setVersion(int versionCode, String versionName);
    }
    interface IIPermission {
        IIConfigure setCustomPermissionInterface(IPermissionInterface iPermission);
        IIConfigure setPermissionList(String[] permissionList);
        IIConfigure noPermissionRequired();
    }
    interface IIConfigure {
        IIOperation setConfiguration(IConfigure iConfigure);
        IIOperation setConfigurationUI(Activity activity, IConfigureWithUI iConfigureWithUI);
        IIOperation noConfiguration();
    }

    interface IIOperation {
        IIOperation setBackgroundExecutionInterface(IBackgroundProcess iBackgroundProcess);

        IIOperation addUserInterface(String id, String name, String description, MCUserInterface mcUserInterface);

        IIOperation addAction(String id, String name, String description, MCAction mcAction);

        MCExtensionAPILibrary build();
    }
}