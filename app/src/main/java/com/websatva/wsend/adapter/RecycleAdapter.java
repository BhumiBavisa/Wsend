package com.websatva.wsend.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.websatva.wsend.activities.ContactList;
import com.websatva.wsend.R;
import com.websatva.wsend.model.Model;

import java.util.List;

//RecylerAdapter get Layout of data which we get from csv file

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.Myholder>
{
    List<Model> dataModelArrayList;
    Context ctx;

    public RecycleAdapter(Context ctx,List<Model> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
        this.ctx = ctx;
        setHasStableIds(true);
    }

    class Myholder extends RecyclerView.ViewHolder
    {
        TextView name,number;
        Button btnsend;

        public Myholder(View itemView)
        {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            btnsend = (Button) itemView.findViewById(R.id.sendmsg);
        }
    }


    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null);
         return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(final Myholder holder, int position)
    {
        final Model dataModel=dataModelArrayList.get(position);
        holder.name.setText(dataModel.getName());
        holder.number.setText(dataModel.getNumber());

        holder.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String Url="http://api.whatsapp.com/send?phone=+91"+dataModel.getNumber()+"&text="+ ContactList.newString;
               // String Url="https://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Url));
                ctx.startActivity(i);
                holder.btnsend.setText("SENT");

                /*boolean installed = appInstalledOrNot("com.whatsapp");
                if(installed) {
                    //https://wa.me/+918758020327/?text=%22hi%22
                    String no=imageModelArrayList.get(position).getNumber();
                    Log.d("TEXT NUMBER....", no);

                    Log.d("TEXT MESSAGE....", ContactList.newString);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://api.whatsapp.com/send?phone="+"+91"+no+"&text=" + ContactList.newString));

                    ctx.startActivity(browserIntent);
                    holder.btn_send.setText("SENT");
                } else
                {
                    Toast.makeText(ctx,"Whatsapp is not installed on your device",Toast.LENGTH_SHORT).show();
                }*/
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
