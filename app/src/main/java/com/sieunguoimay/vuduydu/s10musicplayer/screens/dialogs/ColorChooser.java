package com.sieunguoimay.vuduydu.s10musicplayer.screens.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import com.sieunguoimay.vuduydu.s10musicplayer.R;

public class ColorChooser extends AppCompatDialogFragment{
    private AlertDialog alertDialog;
    private CardView cardView_CancelButton_color;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color_chooser,null);
        view.findViewById(R.id.color1).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete1);}});
        view.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete2);}});
        view.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete3);}});
        view.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete4);}});
        view.findViewById(R.id.color5).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete5);}});
        view.findViewById(R.id.color6).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete6);}});
        view.findViewById(R.id.color7).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete7);}});
        view.findViewById(R.id.color8).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete8);}});
        view.findViewById(R.id.color9).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete9);}});
        view.findViewById(R.id.color10).setOnClickListener(new View.OnClickListener(){@Override public void onClick(View v){getColor(R.color.colorPallete10);}});

        alertDialog = builder.setView(view).setTitle("Choose color").create();

        cardView_CancelButton_color = view.findViewById(R.id.cardView_CancelButton_color);
        cardView_CancelButton_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }
    private void getColor(int color){
        listener.applyColor(getResources().getColor(color));
        alertDialog.dismiss();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ColorChooser.ColorChooserListner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement ColorChooserListener");
        }

    }
    private ColorChooserListner listener;

    public interface ColorChooserListner{
        void applyColor(int color);
    }
}
