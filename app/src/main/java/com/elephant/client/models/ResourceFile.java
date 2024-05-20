package com.elephant.client.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import lombok.Getter;

@Getter
public class ResourceFile {
    private final Uri uri;
    private byte[] bytes;
    private final String name;
    private final long sizeLong; //The size of the file in bytes
    private final int sizeInt;
    private final Context context;


    public ResourceFile(Uri uri, Context context) throws IOException {
        this.context = context;
        this.uri = uri;

        boolean canOpenInputStream = Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)
                || Objects.equals(uri.getScheme(), ContentResolver.SCHEME_ANDROID_RESOURCE)
                || Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE);

        if (!canOpenInputStream) {
            throw new IOException("Can't open InputStream on this Uri scheme.");
        }

        try (Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (returnCursor == null) {
                throw new IOException("Can't get filename and size.");
            }

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            name = returnCursor.getString(nameIndex);
            sizeLong = returnCursor.getLong(sizeIndex);

            try {
                sizeInt = Math.toIntExact(sizeLong);
            } catch (ArithmeticException e) {
                throw new IOException("Object size is too big.", e);
            }

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                if (inputStream == null) {
                    throw new IOException("InputStream provider crashed, can't read data.");
                }
                readBytesFromInputStream(inputStream);
            }
            catch (IOException e) {
                throw new IOException("Failed to read data from InputStream.", e);
            }
        } catch (Exception e) {
            throw new IOException("Failed to get name and size.", e);
        }


    }

    private void readBytesFromInputStream(InputStream inputStream) throws IOException {
        bytes = new byte[sizeInt];
        int bytesRead = inputStream.read(bytes, 0, bytes.length);
        if (bytesRead != sizeInt) {
            throw new IOException("Could not read expected amount of data from file.");
        }
    }


}
