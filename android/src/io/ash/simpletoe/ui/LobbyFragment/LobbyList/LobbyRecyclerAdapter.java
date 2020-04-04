package io.ash.simpletoe.ui.LobbyFragment.LobbyList;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.ash.simpletoe.R;

public class LobbyRecyclerAdapter extends RecyclerView.Adapter<LobbyRecyclerAdapter.LobbyHolder> {
    private final Context context;
    private List<Lobby> lobbies;
    private RecyclerViewClickListener mListener;

    public LobbyRecyclerAdapter(Context context, List<Lobby> lobbies, RecyclerViewClickListener listener) {
        this.lobbies = lobbies;
        this.mListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public LobbyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lobby_holder, viewGroup, false);
        return new LobbyHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyHolder lobbyHolder, int i) {
        Resources res = lobbyHolder.itemView.getContext().getResources();

        Lobby temporary = this.lobbies.get(i);
        lobbyHolder.lobbyName.setText(temporary.getName());


        if (temporary.isLocked())
            lobbyHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorNotAllowed_custom));
        else
            lobbyHolder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary_custom));

        if (temporary.isLocked())
            lobbyHolder.lobbyText.setText(R.string.Password_needed);
        else {
            lobbyHolder.lobbyState.setVisibility(View.GONE);
            lobbyHolder.lobbyText.setText(R.string.Enter_To_Lobby);
        }
    }

    @Override
    public int getItemCount() {
        return this.lobbies.size();
    }

    static class LobbyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecyclerViewClickListener mListener;

        CardView cv;
        TextView lobbyName;
        TextView lobbyText;
        ImageView lobbyState;

        LobbyHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            cv = itemView.findViewById(R.id.cv);
            lobbyName = itemView.findViewById(R.id.lobbyName);
            lobbyState = itemView.findViewById(R.id.lobbyState);
            lobbyText = itemView.findViewById(R.id.lobbyButtonName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
