package com.chebyr.vcardrealm.contacts.view;


import android.support.v4.app.Fragment;

/* VCardEditor provides for editing the VCardSettings used for VCardView */

public class VCardEditor extends Fragment //implements SeekBar.OnSeekBarChangeListener
{
    public static final String TAG = "VCardEditor";
/*
    StudioActivity mActivity;
    TemplateParser vCardDocument;

    SeekBar bgTransparency;
    RecyclerView fieldListView;

    public VCardEditor()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mActivity = (StudioActivity) getActivity();

        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.vcard_editor, container, false);
        bgTransparency = (SeekBar) rootView.findViewById(R.id.bg_transparency);
        fieldListView = (RecyclerView) rootView.findViewById(R.id.field_list);

        fieldListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fieldListView.setLayoutManager(linearLayoutManager);
        fieldListView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.d(TAG, "onResume");

        mActivity.setTitle("Editor");

        vCardDocument = mActivity.mVCardDocument;

        bgTransparency.setProgress((int) vCardDocument.getBackgroundAlpha());
        bgTransparency.setOnSeekBarChangeListener(this);

        List<VCardFieldFormat> VCardFieldFormatList = new ArrayList<VCardFieldFormat>();
        VCardFieldFormat vCardFieldFormat;

        vCardFieldFormat = vCardDocument.getVCardFieldFormat("vcard");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("contact_photo");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("incoming_number");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("display_name");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("nick_name");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("job_title");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("organization");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("address");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("groups");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("phone_numbers");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("emails");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("instant_messengers");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("website");
        VCardFieldFormatList.add(vCardFieldFormat);
        vCardFieldFormat = vCardDocument.getVCardFieldFormat("notes");
        VCardFieldFormatList.add(vCardFieldFormat);

        VCardFieldAdapter adapter = new VCardFieldAdapter(VCardFieldFormatList);
        adapter.mActivity = mActivity;
        adapter.vCardDocument = vCardDocument;
        fieldListView.setAdapter(adapter);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if(seekBar.getId() != R.id.bg_transparency)
            return;

        vCardDocument.setBackgroundAlpha((long)progress);
        Log.d(TAG, "Alpha = " + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
    */
}