package hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple;



import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Firebase_Helper {

    static FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static void loadFirebase(Context cxt,  AdManager.firebaseonloadcomplete loadcomplete) {

        MobileAds.initialize(cxt, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        remoteConfig(loadcomplete);
    }

    public static void remoteConfig( AdManager.firebaseonloadcomplete loadcomplete) {
        try {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(30).build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {

                        String response = mFirebaseRemoteConfig.getString("appdata");
//                        Log.e("RemoteData: ", response);

                        if (!response.isEmpty()) {
                            doNext(response, loadcomplete);
                        }
                    }
                }
            }).addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Exception: ", e.getMessage());
                }
            });

            mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
                @Override
                public void onUpdate(ConfigUpdate configUpdate) {

                    Log.d("TAG", "Updated keys: " + configUpdate.getUpdatedKeys());

                    mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                String response = mFirebaseRemoteConfig.getString("App_Data");
                                if (!response.isEmpty()) {
//                                    Log.e("RemoteData: update ", response);
                                    doNext(response, loadcomplete);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onError(FirebaseRemoteConfigException error) {
                }
            });

        } catch (Exception e) {
            loadcomplete.onloadcomplete();
            e.printStackTrace();
        }
    }

    public static void doNext(String response,  AdManager.firebaseonloadcomplete loadcomplete) {
        GlobalVar.appData = parseAppUserListModel(response);
        loadcomplete.onloadcomplete();

    }

    public static RemoteAppDataModel parseAppUserListModel(String jsonObject) {
        try {
            Gson gson = new Gson();
            TypeToken<RemoteAppDataModel> token = new TypeToken<RemoteAppDataModel>() {
            };
            return gson.fromJson(jsonObject, token.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}