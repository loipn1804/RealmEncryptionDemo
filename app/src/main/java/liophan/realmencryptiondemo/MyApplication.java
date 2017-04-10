package liophan.realmencryptiondemo;

import android.app.Application;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Copyright (c) 2017, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since April 09, 2017
 */

public class MyApplication extends Application {

    private final String DATABASE_1 = "DB1.realm";
    private final String DATABASE_2 = "DB2.realm";

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final boolean IS_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final String ALIAS = "RealmEncrypt";

    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();
    }

    private void initRealm() {
        Realm.init(this);

        String initKeyStore = initKeyStore();
        if (!TextUtils.isEmpty(initKeyStore) && initKeyStore.length() >= 64) {
            String key = initKeyStore.substring(0, 64);

            Log.e("LIO", "key " + key.length() + " " + key);

            try {
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .name(DATABASE_1)
                        .schemaVersion(0)
                        .encryptionKey(key.getBytes())
                        .migration(new Migration())
                        .build();

                Realm.setDefaultConfiguration(realmConfiguration);

                Realm realm = Realm.getDefaultInstance();
                realm.close();
            } catch (Exception e) {
                Log.e("LIO", e.getMessage());
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .name(DATABASE_1)
                        .schemaVersion(0)
                        .encryptionKey(key.getBytes())
                        .migration(new Migration())
                        .build();

                // Start with a clean slate every time
                Realm.deleteRealm(realmConfiguration);

                Realm.setDefaultConfiguration(realmConfiguration);
            }
        } else {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(DATABASE_1)
                    .schemaVersion(0)
                    .migration(new Migration())
                    .build();

            Realm.setDefaultConfiguration(realmConfiguration);
        }
    }

    @SuppressWarnings("All")
    private String initKeyStore() {
        try {
            KeyStore keyStore = java.security.KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(ALIAS)) {
                // Create new key and save to KeyStore
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
                if (IS_M) {
                    KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setRandomizedEncryptionRequired(false)
                            .build();

                    kpg.initialize(keyGenParameterSpec);
                } else {
                    // Generate a key pair for encryption
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 30);
                    KeyPairGeneratorSpec keyPairGeneratorSpec = new KeyPairGeneratorSpec.Builder(getApplicationContext())
                            .setAlias(ALIAS)
                            .setSubject(new X500Principal("CN=" + ALIAS))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();

                    kpg.initialize(keyPairGeneratorSpec);
                }
                kpg.generateKeyPair();
                // Create key and save to KeyStore
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(ALIAS, null);
                return Base64.encodeToString(privateKeyEntry.getCertificate().getPublicKey().getEncoded(), Base64.DEFAULT);
            } else {
                // Get key from KeyStore
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(ALIAS, null);
                return Base64.encodeToString(privateKeyEntry.getCertificate().getPublicKey().getEncoded(), Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
