package ru.nwts.wherewe.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.List;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.database.DataManager;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.settings.Constants;
import ru.nwts.wherewe.util.PreferenceHelper;

import static android.app.Activity.RESULT_OK;
import static ru.nwts.wherewe.settings.Constants.GALLERY_REQUEST;

/**
 * Created by пользователь on 20.06.2017.
 */

public class EditAbonentFragment extends Fragment {

    SmallModel mSmallModel;
    List<SmallModel> smallModels = new ArrayList<>();
    public DBHelper dbHelper;
    PreferenceHelper preferenceHelper;
    private Activity activity_context;
    private boolean isExistSmallModel = false;
    private String mEmail;
    private EditText edTextName, edTextEmail;
    private CheckBox mCheckBoxShowOnMap;
    private Button mButtonSave;
    private SaveListener mSaveListener;
    private ImageView mImageView;
    private Uri imgViewUri;
    private String imgViewUriString;

    private static final String TAG = Constants.TAG_EDIT_ABONENT_FRAGMENT;
    private OnSelectedActionGalleryListener mOnSelectedActionGalleryListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_context = getActivity();
        PreferenceHelper.getInstance().init(activity_context);
        preferenceHelper = PreferenceHelper.getInstance();
        dbHelper = TODOApplication.getInstance().dbHelper;
        request();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TAG, true);
        //Сохранаяем состояние карточки абонента
        preferenceHelper.putString(Constants.KEY_EDIT_NAME_ABONENT, edTextName.getText().toString());
        preferenceHelper.putString(Constants.KEY_EDIT_EMAIL_ABONENT, edTextEmail.getText().toString());
        preferenceHelper.putString(Constants.KEY_EDIT_FOTO_ABONENT, imgViewUri.toString());
        preferenceHelper.putBoolean(Constants.KEY_EDIT_SHOW_ABONENT, mCheckBoxShowOnMap.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
//        request();
    }

    private void showAbonent() {
        if (isExistSmallModel) {
            if (smallModels.get(0).getEmail() != null && !TextUtils.isEmpty(smallModels.get(0).getEmail())) {
                edTextEmail.setText(smallModels.get(0).getEmail());
            }
            if (smallModels.get(0).getName() != null && !TextUtils.isEmpty(smallModels.get(0).getName())) {
                edTextName.setText(smallModels.get(0).getName());
            }
            if (smallModels.get(0).getRights() == 0) {
                mCheckBoxShowOnMap.setChecked(false);
            } else {
                mCheckBoxShowOnMap.setChecked(true);
            }
            if (smallModels.get(0).getImgView() != null && !TextUtils.isEmpty(smallModels.get(0).getImgView())) {
                imgViewUri = Uri.parse(smallModels.get(0).getImgView());
                if (activity_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    picassoGetImages(mImageView, imgViewUri, 200, 150);
                } else {
                    picassoGetImages(mImageView, imgViewUri, 250, 200);
                }
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();

//        EventBus.getDefault().unregister(this);
    }

    /**
     * Выбор галереи
     * 0 - отказ
     * 1 - Фото
     * 2 - встроенная галерея
     */
    public interface OnSelectedActionGalleryListener {
        void onSelectedActionGallery(int buttonIndex);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_abonent, container,false);
        view.setTag(TAG);
        edTextEmail = (EditText) view.findViewById(R.id.edTxtEmailAbonent);
        edTextName = (EditText) view.findViewById(R.id.edTxNameAbonent);
        mCheckBoxShowOnMap = (CheckBox) view.findViewById(R.id.chkBoxShowOnMap);
//        if (activity_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        mImageView = (ImageView) view.findViewById(R.id.imgAbonent);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
//        }
        mButtonSave = (Button) view.findViewById(R.id.btnSaveAbonent);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExistSmallModel) {
                    if (smallModels.get(0) != null) {
                        if (edTextEmail.getText() != null) {
                            smallModels.get(0).setEmail(edTextEmail.getText().toString());
                        }
                        if (edTextName.getText() != null) {
                            smallModels.get(0).setName(edTextName.getText().toString());
                        }
                        if (mCheckBoxShowOnMap.isChecked()) {
                            smallModels.get(0).setRights(1);
                        } else {
                            smallModels.get(0).setRights(0);
                        }
                        if (imgViewUri != null && !TextUtils.isEmpty(imgViewUri.toString())) {
                            imgViewUriString = imgViewUri.toString();
                            smallModels.get(0).setImgView(imgViewUriString);
                        }
                    }
                }

                mSaveListener = (SaveListener) activity_context;
                mSaveListener.onSaveAbonent(smallModels.get(0));
            }
        });
        showAbonent();
        /**
         * Восстановление состояние
         */
        if (TextUtils.isEmpty(edTextName.getText().toString())){
            edTextName.setText(preferenceHelper.getString(Constants.KEY_EDIT_NAME_ABONENT));
        }
        if (TextUtils.isEmpty(edTextEmail.getText().toString())){
            edTextEmail.setText(preferenceHelper.getString(Constants.KEY_EDIT_EMAIL_ABONENT));
        }
        if (TextUtils.isEmpty(imgViewUriString)){
            imgViewUriString = preferenceHelper.getString(Constants.KEY_EDIT_FOTO_ABONENT);
        }
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imgViewUri = selectedImage;
                    if (activity_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        picassoGetImages(mImageView, imgViewUri, 200, 150);
                    } else {
                        picassoGetImages(mImageView, imgViewUri, 250, 200);
                    }
                    //   mImageView.setImageURI(selectedImage);
                }
        }
    }

    public interface SaveListener {
        void onSaveAbonent(SmallModel updatedModel);
    }

    private boolean getSmallModel(String email) {
        if (dbHelper.getSmallModelFromEmail(email) != null) {
            smallModels.clear();
            smallModels.add(dbHelper.getSmallModelFromEmail(email));
            return true;
        }
        return false;
    }

    private void request() {
        if (TODOApplication.getInstance().getEmail() != null
                && !TextUtils.isEmpty(TODOApplication.getInstance().getEmail())) {
            isExistSmallModel = getSmallModel(TODOApplication.getInstance().getEmail());
        }
    }





//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent

    /**
     * Picasso
     */
    private void picassoGetImages(final ImageView flowerImageView, final Uri imageUri, final int x, final int y) {

        DataManager.getInstance().getPicasso()
                .load(imageUri)
                .centerInside()
                .resize(x, y)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.error_load_image)
                .placeholder(R.drawable.error_load_image)
                .into(flowerImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(Constants.TAG, "load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(imageUri)
                                //  .fit()
                                .centerCrop()
                                .resize(x, y)
                                .error(R.drawable.error_load_image)
                                .placeholder(R.drawable.error_load_image)
                                .into(flowerImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(Constants.TAG, "Save from network - fetch image");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(Constants.TAG, "Could not fetch image");
                                    }
                                });
                    }
                });
    }
}
