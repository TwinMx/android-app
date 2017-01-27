package fr.isen.twinmx.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.isen.twinmx.R;
import fr.isen.twinmx.util.manual.ManualPage;

/**
 * Created by pierredfc.
 */

public class ManualHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.manual_photo)
    ImageView photo;

    @BindView(R.id.manual_id)
    TextView instructionId;

    @BindView(R.id.manual_text)
    TextView instruction;

    private ManualPage page;

    public ManualHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ManualPage manualPage, int position) {
        this.page = manualPage;
        this.instructionId.setText(String.valueOf(position + 1));
        this.instruction.setText(manualPage.getText());
        // ImageView TODO
    }
}
