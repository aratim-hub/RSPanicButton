package com.theappwelt.vhelp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.theappwelt.vhelp.model.Contact;
import java.util.List;

public class ProductListAdapter
        extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private int productItemLayout;
    private List<Contact> productList;

    public ProductListAdapter(int layoutId) {
        productItemLayout = layoutId;
    }

    public void setProductList(List<Contact> products) {
        productList = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(productItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.name;
        item.setText(productList.get(listPosition).getName());
        TextView number = holder.number;
        number.setText(productList.get(listPosition).getMobile());
        TextView relationship = holder.relationship;
        String relationshipStr = productList.get(listPosition).getRelationship();
        if (!relationshipStr.equals("")){
            relationship.setText(relationshipStr);
        }
        TextView preferences = holder.preferences;
        boolean isCall = productList.get(listPosition).isCall();
        boolean isSms = productList.get(listPosition).isSms();
        String call = "";
        if (isCall && isSms){
            call = "Call and Sms";
        }else if (isCall){
            call = "Call";
        }else if (isSms){
            call = "Sms";
        }else {
            call = "";
        }



        preferences.setText(call);
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,number,relationship,preferences;
        RelativeLayout itemLayout;
        ImageView imgEdit;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            relationship = itemView.findViewById(R.id.relationship);
            preferences = itemView.findViewById(R.id.preferences);
            itemLayout = itemView.findViewById(R.id.itemLayout);
            imgEdit = itemView.findViewById(R.id.imgEdit);

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(productList.get(position),position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Contact model,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

