package com.heilov.heilov.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heilov.heilov.Model.Conversation;
import com.heilov.heilov.Model.User;
import com.heilov.heilov.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends BaseAdapter {

    private final ArrayList<Conversation> conversations;
    private Activity context;
    private FirebaseAuth auth;

    public ChatsAdapter(Activity context, ArrayList<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @Override
    public int getCount() {
        if (conversations != null) {
            return conversations.size();
        } else {
            return 0;
        }
    }

    @Override
    public Conversation getItem(int position) {
        if (conversations != null) {
            return conversations.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Conversation conv = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_contact, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        User p;
        if (conv.getFirstPerson().getUid().equals(currentUser.getUid())) {
            p = conv.getSecondPerson();

        } else {
            p = conv.getFirstPerson();

        }
        if (p.getProfilePic() != null && !p.getProfilePic().isEmpty()) {

            Glide.with(context).load(p.getProfilePic()).into(holder.profileImage);
        }
        holder.txtFullName.setText(p.getName());
        if (conv.getListMessageData() != null) {
            holder.lastMessage.setText(conv.getListMessageData().get(conv.getListMessageData().size() - 1).getMessageText());
        } else {
            holder.lastMessage.setText("No Message. Write first");
        }
        return convertView;
    }

    public void add(Conversation contact) {
        conversations.add(contact);
    }

    public void add(ArrayList<Conversation> contacts) {
        conversations.addAll(contacts);
    }

    public void clear() {
        conversations.clear();
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.profileImage =  v.findViewById(R.id.contact_image);
        holder.txtFullName = v.findViewById(R.id.contact_name);
        holder.lastMessage = v.findViewById(R.id.lastmessage);
        return holder;
    }

    private static class ViewHolder {
        CircleImageView profileImage;
        TextView txtFullName;
        TextView lastMessage;

    }

}

