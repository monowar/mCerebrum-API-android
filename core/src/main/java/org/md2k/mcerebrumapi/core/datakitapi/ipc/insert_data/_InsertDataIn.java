package org.md2k.mcerebrumapi.core.datakitapi.ipc.insert_data;

import android.os.Bundle;
import android.util.SparseArray;

import org.md2k.mcerebrumapi.core.data.DataArray;
import org.md2k.mcerebrumapi.core.datakitapi.ipc.OperationType;
import org.md2k.mcerebrumapi.core.datakitapi.ipc._Session;
import org.md2k.mcerebrumapi.core.status.MCStatus;


/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source value must retain the above copyright notice, this
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
public class _InsertDataIn {

    public static _Session create(int session, SparseArray<DataArray> data) {
        Bundle b = new Bundle();
        b.putSparseParcelableArray(DataArray.class.getSimpleName(), data);
        return new _Session(session, OperationType.INSERT_DATA, MCStatus.SUCCESS, b);
    }

    public static SparseArray<DataArray> getData(Bundle b) {
        if (b == null) return null;
        return b.getSparseParcelableArray(DataArray.class.getSimpleName());
    }
}
