package gruppo_20.iassistant.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gruppo_20.iassistant.R;

/**
 * Created by vito on 15/09/2018.
 */
public class DataHolder extends RecyclerView.ViewHolder {

    private TextView mData;

    public DataHolder(@NonNull View itemView) {

        super(itemView);
        mData = (TextView) itemView.findViewById(R.id.data);
    }

    public TextView getmData() {
        return mData;
    }

    public void setmData(TextView mData) {
        this.mData = mData;
    }
}
