package com.elephant.client.ui;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elephant.client.R;
import com.elephant.client.models.File;
import com.elephant.client.models.Folder;
import com.elephant.client.models.FsObject;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class FilesystemAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

    @Getter
    private List<FsObject> objects = new ArrayList<>();
    @Setter
    private OnItemClickListener onItemClickListener;
    @Setter
    Handler handler;

    public FilesystemAdapter(OnItemClickListener listener, Handler handler) {
        this.onItemClickListener = listener;
        this.handler = handler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item, parent, false);

        switch (viewType) {
            case FsObject.TYPE_FILE:
                return new FilesystemAdapter.FileHolder(itemView);
            case FsObject.TYPE_FOLDER:
                return new FilesystemAdapter.FolderHolder(itemView);
            default:
                return new FilesystemAdapter.FolderHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case FsObject.TYPE_FILE:
                ((FileHolder) holder).bindView(position);

                break;
            case FsObject.TYPE_FOLDER:
                ((FolderHolder) holder).bindView(position);
                break;
        }


//        holder.itemView.setOnClickListener(v -> {
//            int id = results.get((int)getItemId(position)).getId();
//            Network.getInstance().getFolders(handler, id);
//        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return objects.get(position).getType();
    }

    public void setObjects(List<? extends FsObject> objects) {
        this.objects.clear();
        this.objects.addAll(objects);

        notifyDataSetChanged();
    }

    public class FileHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView fileName;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            fileName = itemView.findViewById(R.id.folder_name);

            imageView.setImageResource(R.drawable.ic_file);
        }

        void bindView(int position) {
            File file = (File) objects.get(position);
            // bind data to the views
            // textView.setText()...
            fileName.setText("File: " + file.getName());
            itemView.setOnClickListener(v -> {
            });
        }
    }

    public class FolderHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView folderName;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            folderName = itemView.findViewById(R.id.folder_name);

            imageView.setImageResource(R.drawable.ic_folder);
        }

        void bindView(int position) {
            Folder folder = (Folder) objects.get(position);
            // bind data to the views
            // textView.setText()...
            itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(folder.getId());
            });
            folderName.setText("Folder: " + folder.getName());

        }
    }
}
