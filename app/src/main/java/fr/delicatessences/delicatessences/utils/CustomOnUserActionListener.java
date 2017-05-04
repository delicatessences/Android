package fr.delicatessences.delicatessences.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import net.mediavrog.irr.DefaultOnUserActionListener;

import fr.delicatessences.delicatessences.R;


public class CustomOnUserActionListener extends DefaultOnUserActionListener {

    private static final String MAIL_ADDRESS = "contact@delicatessences.fr";
    private static final String MAIL_MESSAGE_TYPE = "message/rfc822";

    public CustomOnUserActionListener() {
        super(null, null);
    }


    @Override
    public void onFeedback(Context ctx) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(MAIL_MESSAGE_TYPE);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIL_ADDRESS});
        try {
            ctx.startActivity(Intent.createChooser(i, ctx.getString(R.string.mail_action)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, R.string.email_no_client, Toast.LENGTH_SHORT).show();
        }
    }
}
