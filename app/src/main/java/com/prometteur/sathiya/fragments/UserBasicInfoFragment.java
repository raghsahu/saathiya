package com.prometteur.sathiya.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.databinding.FragmentEditBasicDetailBinding;
import com.prometteur.sathiya.databinding.FragmentUserBasicInfoBinding;
import com.prometteur.sathiya.utills.AppConstants;

import org.json.JSONException;

import static com.prometteur.sathiya.profile.ProfileActivity.resItem;


public class UserBasicInfoFragment extends Fragment {


FragmentUserBasicInfoBinding profileBinding;
    public UserBasicInfoFragment() {
        // Required empty public constructorw
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileBinding= FragmentUserBasicInfoBinding.inflate(inflater, container, false);
        return profileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String FName = null;
        String LName = null;
        try {
            if(resItem!=null) {
                FName = resItem.getString("firstname");
                LName = resItem.getString("lastname");

                //String[] name_array = LFName.split(" ");

                profileBinding.edtFullName.setText(FName + " " + LName);
                //edtLastName.setText(name_array[1]);


                profileBinding.tvUserEmail.setText(getNullAvoid(resItem.getString("email")));
                profileBinding.tvUserDOB.setText(getNullAvoid(resItem.getString("birthdate")));
                String strGender = resItem.getString("gender");
                profileBinding.tvUserGender.setText("" + getNullAvoid(strGender));
                AppConstants.MotherTongueId = resItem.getString("m_tongue_id");
                profileBinding.tvUserMobile.setText(resItem.getString("mobile"));

                profileBinding.tvUserState.setText(getNullAvoid(resItem.getString("state_name")));
                profileBinding.tvUserMarital.setText(getNullAvoid(resItem.getString("m_status")));
                AppConstants.StateId = resItem.getString("state_id");

                profileBinding.tvUserCity.setText(getNullAvoid(resItem.getString("city_name")));
                AppConstants.CityId = resItem.getString("city");

                if (resItem.getString("profile_text") != null) {
                    profileBinding.tvUserDescription.setText(getNullAvoid(resItem.getString("profile_text")));
                }
                if (resItem.getString("deal_maker") != null) {
                    profileBinding.tvUserDealMaker.setText(getNullAvoid(resItem.getString("deal_maker")));
                }
                if (resItem.getString("height") != null) {
                    profileBinding.tvUserHeight.setText(resItem.getString("height"));
                }
                if (resItem.getString("height") != null) {
                    profileBinding.tvUserDiet.setText(resItem.getString("diet"));
                }

                //education details
                profileBinding.tvUserEducation.setText(getNullAvoid(resItem.getString("edu_detail")));
                profileBinding.tvUserProfession.setText(getNullAvoid(resItem.getString("occupation")));
                profileBinding.tvUserEmplyedIN.setText(getNullAvoid(resItem.getString("emp_in")));
                if (resItem.getString("shadibudget").isEmpty() || resItem.getString("shadibudget").equalsIgnoreCase("Not Available")) {
                    profileBinding.tvUserIncome.setText(getNullAvoid(resItem.getString("income")));
                } else {
                    profileBinding.tvUserIncome.setText("" + getNullAvoid(resItem.getString("income")));
                }
                profileBinding.tvUserReligion.setText(getNullAvoid(resItem.getString("religion_name")));
                profileBinding.tvUserCaste.setText(getNullAvoid(resItem.getString("caste_name")));
                profileBinding.tvUserSubCaste.setText(getNullAvoid(resItem.getString("subcaste")));

                //other details
                profileBinding.tvUserMotherTongue.setText(getNullAvoid(resItem.getString("m_tongue")));
                profileBinding.tvUserManglik.setText(getNullAvoid(resItem.getString("manglik")));
                profileBinding.tvUserFamIncome.setText(getNullAvoid(resItem.getString("income")));
                if (resItem.getString("profileby") != null) {
                    profileBinding.tvUserMangedBy.setText(getNullAvoid(resItem.getString("profileby")));
                }
                profileBinding.tvUserParntMob.setText(getNullAvoid(resItem.getString("mobile_parent")));
                if (resItem.getString("shadibudget").isEmpty() || resItem.getString("shadibudget").equalsIgnoreCase("Not Available")) {
                    profileBinding.tvUserShadiBudget.setText(getNullAvoid(resItem.getString("shadibudget")));
                } else {
                    profileBinding.tvUserShadiBudget.setText("₹" + resItem.getString("shadibudget") + " lacs");
                }
                profileBinding.tvUserAbled.setText(getNullAvoid(resItem.getString("physicalStatus")));
                if (resItem.getString("time_to_call").equalsIgnoreCase("Any Time")) {
                    profileBinding.tvUserCallTime.setText(getString(R.string.all_time));
                } else {
                    if (resItem.getString("time_to_call").equalsIgnoreCase("Not Available")) {
                        profileBinding.tvUserCallTime.setText(getString(R.string.all_time));
                    } else {
                        profileBinding.tvUserCallTime.setText(resItem.getString("time_to_call") + " hrs");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public String getNullAvoid(String text)
    {
        if(text!=null && !text.isEmpty()&& !text.equalsIgnoreCase("null") && !text.equalsIgnoreCase("Not Available")){
            return text;
        }else
        {
            return getString(R.string.not_available);
        }
    }
}