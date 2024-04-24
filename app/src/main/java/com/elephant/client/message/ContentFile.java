package com.elephant.client.message;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import lombok.Getter;

@Getter
public class ContentFile {
    private final Uri uri;
    private byte[] bytes;
    private String name;
    private long sizeLong; //The size of the file in bytes
    private int sizeInt;
    private final Context context;
    private boolean isReadable;


//TODO a lot of issues here
    public ContentFile(Uri uri, Context context) {
        this.context = context;
        this.uri = uri;
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)
                || Objects.equals(uri.getScheme(), ContentResolver.SCHEME_ANDROID_RESOURCE)
                || Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {

            try (Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null)) {
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                assert returnCursor != null;
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                name = returnCursor.getString(nameIndex);
                sizeLong = returnCursor.getLong(sizeIndex);
                sizeInt = (int) sizeLong;

                readBytesFromInputStream(Objects.requireNonNull(context.getContentResolver().openInputStream(uri)), sizeLong);
                isReadable = true;
            }
            catch (Exception e) {
                Toast.makeText(context, "Failed to get name and size", Toast.LENGTH_LONG).show();
                isReadable = false;
            }
        }
        else {
            isReadable = false;
        }
    }
    private void readBytesFromInputStream(@NonNull InputStream inputStream, long size) throws IOException {
        //TODO check what to do with long to int size conversion
        bytes = new byte[Math.toIntExact(size)];
        int bytesRead = inputStream.read(bytes, 0, bytes.length);
        if (bytesRead != size) {
            throw new IOException("Could not read the expected number of bytes from the stream");
        }
    }







}
