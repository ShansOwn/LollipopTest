package com.shansown.android.lollipoptest.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.shansown.android.lollipoptest.provider.LollipopTestContract;
import com.shansown.android.lollipoptest.sync.SyncHelper;

import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class AccountUtils {

    private static final String TAG = makeLogTag(AccountUtils.class);

    public static final String ACCOUNT_TYPE = "com.shansown.app.lollipoptest.account";
    public static final String ACCOUNT_NAME = "Lollipop";

    public static Account getAccount(final Context context) {
        boolean newAccount = false;
        boolean setupComplete = PrefUtils.isAccountSetupComplit(context);

        // Note: Normally the account name is set to the user's identity (username or email
        // address). However, since we aren't actually using any user accounts, it makes more sense
        // to use a generic string in this case.
        //
        // This string should *not* be localized. If the user switches locale, we would not be
        // able to locate the old account, and may erroneously register multiple accounts.
        final String accountName = ACCOUNT_NAME;
        Account account = new Account(accountName, ACCOUNT_TYPE);
        final AccountManager am = AccountManager.get(context);
        if (am.addAccountExplicitly(account, context.getPackageName(), Bundle.EMPTY)) {
            ContentResolver.setSyncAutomatically(account, LollipopTestContract.CONTENT_AUTHORITY, true);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            PrefUtils.setAccountSetupComplete(context, true);
            SyncHelper.requestSync(context, true);
        }
        return account;
    }
}