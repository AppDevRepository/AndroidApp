package pass.threestech.com.personalactivityscoringsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;


public class ChangePushTimeFragment extends DialogFragment {
    public interface DialogListener {
         void onDialogPositiveClick(DialogFragment dialog, int which);
    }

    public DialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialogFragment_title));
        builder.setSingleChoiceItems(R.array.dialogFragment_pushTimes, -1,new YesOptionClickListener());
        //builder.setItems(R.array.dialogFragment_pushTimes, new YesOptionClickListener());

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private class YesOptionClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mListener.onDialogPositiveClick(ChangePushTimeFragment.this, which);
         }
    }
}
