package gruppo_20.iassistant.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gruppo_20.iassistant.R;

/**
 * Created by vito on 15/09/2018.
 */
public class VisiteHolder extends RecyclerView.ViewHolder{

    private boolean expand = false;
    private TextView mNomePaziente;
    private TextView mStato;
    private ImageView mStatoImage;
    private TextView mOrario;

    private TextView mNPrestazioni;
    private TextView mIndirizzo;
    private TextView mTelefono;
    private FloatingActionButton mMapp;
    private FloatingActionButton mCall;
    private ImageButton mFreccia;

    private RelativeLayout mLayout;

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public TextView getmNomePaziente() {
        return mNomePaziente;
    }

    public void setmNomePaziente(TextView mNomePaziente) {
        this.mNomePaziente = mNomePaziente;
    }

    public TextView getmStato() {
        return mStato;
    }

    public void setmStato(TextView mStato) {
        this.mStato = mStato;
    }

    public ImageView getmStatoImage() {
        return mStatoImage;
    }

    public void setmStatoImage(ImageView mStatoImage) {
        this.mStatoImage = mStatoImage;
    }

    public TextView getmOrario() {
        return mOrario;
    }

    public void setmOrario(TextView mOrario) {
        this.mOrario = mOrario;
    }

    public TextView getmNPrestazioni() {
        return mNPrestazioni;
    }

    public void setmNPrestazioni(TextView mNPrestazioni) {
        this.mNPrestazioni = mNPrestazioni;
    }

    public TextView getmIndirizzo() {
        return mIndirizzo;
    }

    public void setmIndirizzo(TextView mIndirizzo) {
        this.mIndirizzo = mIndirizzo;
    }

    public TextView getmTelefono() {
        return mTelefono;
    }

    public void setmTelefono(TextView mTelefono) {
        this.mTelefono = mTelefono;
    }

    public FloatingActionButton getmMapp() {
        return mMapp;
    }

    public void setmMapp(FloatingActionButton mMapp) {
        this.mMapp = mMapp;
    }

    public FloatingActionButton getmCall() {
        return mCall;
    }

    public void setmCall(FloatingActionButton mCall) {
        this.mCall = mCall;
    }

    public ImageButton getmFreccia() {
        return mFreccia;
    }

    public void setmFreccia(ImageButton mFreccia) {
        this.mFreccia = mFreccia;
    }

    public RelativeLayout getmLayout() {
        return mLayout;
    }

    public void setmLayout(RelativeLayout mLayout) {
        this.mLayout = mLayout;
    }

    public VisiteHolder(@NonNull View itemView) {

            super(itemView);
            mNomePaziente = (TextView) itemView.findViewById(R.id.nomePaziente);
            mStato = (TextView) itemView.findViewById(R.id.stato);
            mStatoImage = (ImageView) itemView.findViewById(R.id.statoImage);
            mNPrestazioni = (TextView) itemView.findViewById(R.id.numPrestazioni);
            mOrario = (TextView) itemView.findViewById(R.id.orario);
            mIndirizzo = (TextView) itemView.findViewById(R.id.indirizzo);
            mTelefono = (TextView) itemView.findViewById(R.id.numero);
            mMapp = (FloatingActionButton) itemView.findViewById(R.id.mappButton);
            mCall = (FloatingActionButton) itemView.findViewById(R.id.callButton);
            mFreccia = (ImageButton) itemView.findViewById(R.id.freccia);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout);

    }
}
