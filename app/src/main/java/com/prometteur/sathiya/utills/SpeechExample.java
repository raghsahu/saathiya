package com.prometteur.sathiya.utills;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.prometteur.sathiya.databinding.ActivitySpeechBinding;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.prometteur.sathiya.utills.AppConstants.setToastStr;


public class SpeechExample extends AppCompatActivity implements TextToSpeech.OnInitListener{
ActivitySpeechBinding activitySpeechBinding;
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
activitySpeechBinding=ActivitySpeechBinding.inflate(getLayoutInflater());
setContentView(activitySpeechBinding.getRoot());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        textToSpeech= new TextToSpeech(SpeechExample.this, this, "com.google.android.tts");
        Set<String> a=new HashSet<>();
        a.add("male");//here you can give male if you want to select male voice.
        Voice v=new Voice("en-us-x-sfg#male_2-local",new Locale("mr","IN"),400,200,true,a);
        textToSpeech.setVoice(v);
        //textToSpeech.setSpeechRate(0.8f);

System.out.println(Arrays.toString(Locale.getAvailableLocales()));
      //  textToSpeech = new TextToSpeech(SpeechExample.this, SpeechExample.this);
        activitySpeechBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TextToSpeechFunction() ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
/*[af, af_NA, af_ZA, agq, agq_CM, ak, ak_GH, am, am_ET, ar, ar_001, ar_AE, ar_BH, ar_DJ, ar_DZ,
    ar_EG, ar_EH, ar_ER, ar_IL, ar_IQ, ar_JO, ar_KM, ar_KW, ar_LB, ar_LY, ar_MA, ar_MR, ar_OM, ar_PS,
    ar_QA, ar_SA, ar_SD, ar_SO, ar_SS, ar_SY, ar_TD, ar_TN, ar_XB, ar_YE, as, as_IN, asa, asa_TZ, ast,
    ast_ES, az, az__#Cyrl, az_AZ_#Cyrl, az__#Latn, az_AZ_#Latn, bas, bas_CM, be, be_BY, bem, bem_ZM, bez,
    bez_TZ, bg, bg_BG, bm, bm_ML, bn, bn_BD, bn_IN, bo, bo_CN, bo_IN, br, br_FR, brx, brx_IN, bs, bs__#Cyrl,
    bs_BA_#Cyrl, bs__#Latn, bs_BA_#Latn, ca, ca_AD, ca_ES, ca_FR, ca_IT, ccp, ccp_BD, ccp_IN, ce, ce_RU, cgg,
    cgg_UG, chr, chr_US, ckb, ckb_IQ, ckb_IR, cs, cs_CZ, cy, cy_GB, da, da_DK, da_GL, dav, dav_KE, de, de_AT,
    de_BE, de_CH, de_DE, de_IT, de_LI, de_LU, dje, dje_NE, dsb, dsb_DE, dua, dua_CM, dyo, dyo_SN, dz, dz_BT,
    ebu, ebu_KE, ee, ee_GH, ee_TG, el, el_CY, el_GR, en, en_001, en_150, en_AG, en_AI, en_AS, en_AT, en_AU, en_BB,
    en_BE, en_BI, en_BM, en_BS, en_BW, en_BZ, en_CA, en_CC, en_CH, en_CK, en_CM, en_CX, en_CY, en_DE, en_DG, en_DK,
    en_DM, en_ER, en_FI, en_FJ, en_FK, en_FM, en_GB, en_GD, en_GG, en_GH, en_GI, en_GM, en_GU, en_GY, en_HK, en_IE,
    en_IL, en_IM, en_IN, en_IO, en_JE, en_JM, en_KE, en_KI, en_KN, en_KY, en_LC, en_LR, en_LS, en_MG, en_MH, en_MO,
    en_MP, en_MS, en_MT, en_MU, en_MW, en_MY, en_NA, en_NF, en_NG, en_NL, en_NR, en_NU, en_NZ, en_PG, en_PH, en_PK,
    en_PN, en_PR, en_PW, en_RW, en_SB, en_SC, en_SD, en_SE, en_SG, en_SH, en_SI, en_SL, en_SS, en_SX, en_SZ, en_TC,
    en_TK, en_TO, en_TT, en_TV, en_TZ, en_UG, en_UM, en_US, en_US_POSIX, en_VC, en_VG, en_VI, en_VU, en_WS, en_XA, en_ZA,
    en_ZG, en_ZM, en_ZW, eo, es, es_419, es_AR, es_BO, es_BR, es_BZ, es_CL, es_CO, es_CR, es_CU, es_DO, es_EA, es_EC,
    es_ES, es_GQ, es_GT, es_HN, es_IC, es_MX, es_NI, es_PA, es_PE, es_PH, es_PR, es_PY, es_SV, es_US, es_UY, es_VE, et,
    et_EE, eu, eu_ES, ewo, ewo_CM, fa, fa_AF, fa_IR, ff, fi, fi_FI, fil, fil_PH, fo, fo_DK, fo_FO, fr, fr_BE, fr_BF,
    fr_BI, fr_BJ, fr_BL, fr_CA, fr_CD, fr_CF, fr_CG, fr_CH, fr_CI, fr_CM, fr_DJ, fr_DZ, fr_FR, fr_GA, fr_GF, fr_GN,
    fr_GP, fr_GQ, fr_HT, fr_KM, fr_LU, fr_MA, fr_MC, fr_MF, fr_MG, fr_ML, fr_MQ, fr_MR, fr_MU, fr_NC, fr_NE, fr_PF,
    fr_PM, fr_RE, fr_RW, fr_SC, fr_SN, fr_SY, fr_TD, fr_TG, fr_TN, fr_VU, fr_WF, fr_YT, fur, fur_IT, fy, fy_NL, ga,
    ga_IE, gd, gd_GB, gl, gl_ES, gsw, gsw_CH, gsw_FR, gsw_LI, gu, gu_IN, guz, guz_KE, gv, gv_IM, ha, ha_GH, ha_NE,
    ha_NG, haw, haw_US, iw, iw_IL, hi, hi_IN, hr, hr_BA, hr_HR, hsb, hsb_DE, hu, hu_HU, hy, hy_AM, ia, ia_001, in,
    in_ID, ig, ig_NG, ii, ii_CN, is, is_IS, it, it_CH, it_IT, it_SM, it_VA, ja, ja_JP, jgo, jgo_CM, jmc, jmc_TZ, jv,
    jv_ID, ka, ka_GE, kab, kab_DZ, kam, kam_KE, kde, kde_TZ, kea, kea_CV, khq, khq_ML, ki, ki_KE, kk, kk_KZ, kkj, kkj_CM,
    kl, kl_GL, kln, kln_KE, km, km_KH, kn, kn_IN, ko, ko_KP, ko_KR, kok, kok_IN, ks, ks_IN, ksb, ksb_TZ, ksf, ksf_CM, ksh,
    ksh_DE, ku, ku_TR, kw, kw_GB, ky, ky_KG, lag, lag_TZ, lb, lb_LU, lg, lg_UG, lkt, lkt_US, ln, ln_AO, ln_CD, ln_CF,
    ln_CG, lo, lo_LA, lrc, lrc_IQ, lrc_IR, lt, lt_LT, lu, lu_CD, luo, luo_KE, luy, luy_KE, lv, lv_LV, mas, mas_KE, mas_TZ,
    mer, mer_KE, mfe, mfe_MU, mg, mg_MG, mgh, mgh_MZ, mgo, mgo_CM, mi, mi_NZ, mk, mk_MK, ml, ml_IN, mn, mn_MN, mr, mr_IN,
    ms, ms_BN, ms_MY, ms_SG, mt, mt_MT, mua, mua_CM, my, my_MM, my_ZG, mzn, mzn_IR, naq, naq_NA, nb, nb_NO, nb_SJ, nd,
    nd_ZW, nds, nds_DE, nds_NL, ne, ne_IN, ne_NP, nl, nl_AW, nl_BE, nl_BQ, nl_CW, nl_NL, nl_SR, nl_SX, nmg, nmg_CM, nn,
    nn_NO, nnh, nnh_CM, nus, nus_SS, nyn, nyn_UG, om, om_ET, om_KE, or, or_IN, os, os_GE, os_RU, pa, pa__#Arab,
    pa_PK_#Arab, pa__#Guru, pa_IN_#Guru, pl, pl_PL, pl_SP, ps, ps_AF, pt, pt_AO, pt_BR, pt_CH, pt_CV, pt_GQ, pt_GW,
    pt_LU, pt_MO, pt_MZ, pt_PT, pt_ST, pt_TL, qu, qu_BO, qu_EC, qu_PE, rm, rm_CH, rn, rn_BI, ro, ro_MD, ro_RO, rof,
    rof_TZ, ru, ru_BY, ru_KG, ru_KZ, ru_MD, ru_RU, ru_UA, rw, rw_RW, rwk, rwk_TZ, sah, sah_RU, saq, saq_KE, sbp,
    sbp_TZ, sd, sd_PK, se, se_FI, se_NO, se_SE, seh, seh_MZ, ses, ses_ML, sg, sg_CF,
    shi, shi__#Latn, shi_MA_#Latn, shi__#Tfng, shi_MA_#Tfng, si, si_LK, sk, sk_SK, sl, sl_SI, smn, s*/
    public void TextToSpeechFunction()
    {

//        String textholder = "Are you happy";
        String textholder = "तू कसा आहेस? मी छान आहे";
//        String textholder = "कृपया अपना सही ईमेल डालें";
//        String textholder = "તમે ખુશ છો";
        try {
           // synthesizeText(textholder);
        } catch (Exception e) {
            e.printStackTrace();
        }
         textToSpeech.speak(textholder, TextToSpeech.QUEUE_FLUSH, null);

        setToastStr(SpeechExample.this , textholder);
    }

    @Override
    public void onDestroy() {

        textToSpeech.shutdown();

        super.onDestroy();
    }

    @Override
    public void onInit(int Text2SpeechCurrentStatus) {

       /* if (Text2SpeechCurrentStatus == TextToSpeech.SUCCESS) {

            textToSpeech.setLanguage(new Locale("en"));
//            textToSpeech.setLanguage(Locale.forLanguageTag("gu"));
            TextToSpeechFunction();
        }*/

        if (Text2SpeechCurrentStatus == TextToSpeech.SUCCESS) {
            Set<String> a=new HashSet<>();
            a.add("male");//here you can give male if you want to select male voice.
            //Voice v=new Voice("en-us-x-sfg#female_2-local",new Locale("en","US"),400,200,true,a);
            Voice v=new Voice("en-us-x-sfg#male_2-local",new Locale("mr","IN"),400,200,true,a);
            textToSpeech.setVoice(v);
            textToSpeech.setSpeechRate(0.8f);

            // int result = T2S.setLanguage(Locale.US);
            int result = textToSpeech.setVoice(v);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                TextToSpeechFunction();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

        try {
            synthesizeText("Are you happy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void synthesizeText(String text) throws Exception {
        // Instantiates a client

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("en-US") // languageCode = "en_us"
                            .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
                            .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
            }
        }
    }

}
