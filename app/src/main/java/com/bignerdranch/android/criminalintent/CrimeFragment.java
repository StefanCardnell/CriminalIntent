package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;
import static androidx.core.app.ShareCompat.*;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";

    private static final String ARG_CRIME_ID = "crime_id";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private static final int PERMISSIONS_FOR_CALLING = 0;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSeriousCheckBox;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mContactSuspectButton;
    private Button mReportButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;

    private Crime mCrime;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mSeriousCheckBox = v.findViewById(R.id.crime_serious);
        mSeriousCheckBox.setChecked(mCrime.isRequiresPolice());
        mSeriousCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
                updateCrime();
            }
        });

        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getBoolean(R.bool.is600dp)) {
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                } else {
                    // Too small for a dialog, use activity
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });

        mTimeButton = v.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getBoolean(R.bool.is600dp)) {
                    FragmentManager manager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    dialog.show(manager, DIALOG_TIME);
                } else {
                    // Too small for a dialog, use activity
                    Intent intent = TimePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_TIME);
                }
            }
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentBuilder i = IntentBuilder.from(getActivity());
                i.setType("text/plain")
                    .setSubject(getString(R.string.crime_report_subject))
                    .setText(getCrimeReport())
                    .setChooserTitle(R.string.send_report)
                    .startChooser();
            }
        });


        // Suspect choose button. Check application for contacts exists first, disable if not.
        final PackageManager packageManager = getActivity().getPackageManager();
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mContactSuspectButton = v.findViewById(R.id.contact_suspect);
        mContactSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSuspect();
            }
        });

        mPhotoView = v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPhotoFile.exists()) return;
                FragmentManager manager = getFragmentManager();
                PictureFragment dialog = PictureFragment.newInstance(mPhotoFile);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoViews();
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this); // Ensures it is not called on detach
            }
        });

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoButton.setEnabled(captureImage.resolveActivity(packageManager) != null);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity: cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);

            }
        });

        updateSuspectViews();
        updateDateViews();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                deleteCrime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_FOR_CALLING
            && grantResults[0] == PermissionChecker.PERMISSION_GRANTED){
            callSuspect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDateViews();
            updateCrime();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateDateViews();
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] contactQueryFields = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            };

            try (Cursor c = getActivity().getContentResolver()
                .query(contactUri, contactQueryFields, null, null, null)) {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();

                String suspectId = c.getString(0);
                String suspectName = c.getString(1);
                Crime.CrimeSuspect suspect = new Crime.CrimeSuspect(suspectId, suspectName);

                mCrime.setSuspect(suspect);
                updateSuspectViews();
                updateCrime();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mPhotoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Delay needed due to activity lifecycle timing issues
                    mPhotoView.announceForAccessibility(getString(R.string.crime_photo_image_description));
                }
            }, 200);
            updatePhotoViews();
            updateCrime();
        }
    }

    private void deleteCrime(){
        CrimeLab.get(getActivity()).deleteCrime(mCrime);
        mCallbacks.onCrimeDeleted(mCrime);
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    /**
     * Only to be called once the Photo Image View has been drawn.
     */
    private void updatePhotoViews(){
        if(!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }

    private void updateSuspectViews() {
        Crime.CrimeSuspect suspect = mCrime.getSuspect();

        if (suspect != null) {
            mSuspectButton.setText(getString(R.string.chosen_suspect_label, suspect.getName()));
            mContactSuspectButton.setText(getString(R.string.contact_suspect_label, suspect.getName()));
            mContactSuspectButton.setVisibility(VISIBLE);
        } else {
            mSuspectButton.setText(R.string.choose_suspect_text);
            mContactSuspectButton.setVisibility(GONE);
        }

    }

    private void updateDateViews() {
        Date date = mCrime.getDate();
        Activity context = getActivity();
        String dateString = DateFormat.getMediumDateFormat(context).format(date);
        String timeString = DateFormat.getTimeFormat(context).format(date);
        mDateButton.setText(getString(R.string.date_button_label, dateString));
        mTimeButton.setText(getString(R.string.time_button_label, timeString));
    }

    private String getCrimeReport() {

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String suspectString;
        Crime.CrimeSuspect suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspectString = getString(R.string.crime_report_no_suspect);
        } else {
            suspectString = getString(R.string.crime_report_suspect, suspect.getName());
        }

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);

    }

    private void callSuspect() {

        String contact_permission = Manifest.permission.READ_CONTACTS;

        if (PermissionChecker.checkSelfPermission(getActivity(), contact_permission)
            != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{contact_permission}, PERMISSIONS_FOR_CALLING);
            return;
        }

        Crime.CrimeSuspect suspect = mCrime.getSuspect();

        String[] numberQueryFields = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor c = getActivity().getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            numberQueryFields,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            new String[]{suspect.getContactID()},
            null
        )) {
            if (c.getCount() == 0) {
                Toast.makeText(getActivity(), "Suspect has no contact number", Toast.LENGTH_SHORT).show();
                return;
            }

            c.moveToFirst();
            Uri number = Uri.parse("tel:" + c.getString(0));
            Intent i = new Intent(Intent.ACTION_DIAL, number);
            startActivity(i);
        }

    }

}
