package com.chebyr.vcardrealm.contacts.cloud.facebook;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacebookClient
{
    private static String TAG = FacebookClient.class.getSimpleName();
    private static String TEST_USER_ID = "103168560709389";

    private static String FIELDS = "fields";
    private static String USER_ID = "id";
    private static String USER_NAME = "name";
    private static String USER_PROFILE_GRAPH = USER_ID + "," + USER_NAME;
    private static String FRIEND_LIST_GRAPH = "friends";
    private static String DATA = "data";

    private AccessToken accessToken;
    private Callback callback;

    public FacebookClient(Callback callback)
    {
        this.callback = callback;

        getAccessToken();
        getUserProfile(TEST_USER_ID);
        getFriends(TEST_USER_ID);
    }

    public void getAccessToken()
    {
        accessToken = AccessToken.getCurrentAccessToken();
    }

    public void getUserProfile(String userID)
    {
        Bundle parameters = new Bundle();
        parameters.putString(FIELDS, USER_PROFILE_GRAPH);
        /* make the API call */
        String graphPath = "/" + userID;
        GraphRequest graphRequest = new GraphRequest(accessToken, graphPath, null,HttpMethod.GET, (GraphResponse graphResponse) ->
        {
            JSONObject responseJSONObject = graphResponse.getJSONObject();
            callback.onUserProfileReceived(new FacebookUserData(responseJSONObject));
        });

        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void getFriends(String userID)
    {
        // Only friends who installed this app are returned in API v2.0 and higher.
        // total_count in summary represents the total number of friends, including those who haven't installed the app.

        Bundle parameters = new Bundle();
        parameters.putString(FIELDS, FRIEND_LIST_GRAPH);
        /* make the API call */
        String graphPath = "/" + userID;
        GraphRequest graphRequest = new GraphRequest(accessToken, graphPath, null, HttpMethod.GET, (GraphResponse graphResponse) ->
        {
            JSONObject responseJSONObject = graphResponse.getJSONObject();
            try
            {
                List<FacebookUserData> userDataList = new ArrayList<>();

                JSONObject friendsObject = responseJSONObject.getJSONObject(FRIEND_LIST_GRAPH);
                JSONArray friendsDataArray = friendsObject.getJSONArray(DATA);
                for(int count = 0; count < friendsDataArray.length(); count++)
                {
                    JSONObject friendsData = friendsDataArray.getJSONObject(count);
                    userDataList.add(new FacebookUserData(friendsData));
                }

                callback.onUserFriendsReceived(userDataList);
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
            }
        });

        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public class FacebookUserData
    {
        public String userID;
        public String userName;

        public FacebookUserData(String userID, String userName)
        {
            this.userID = userID;
            this.userName = userName;
        }

        public FacebookUserData(JSONObject userJSONObject)
        {
            try
            {
                this.userID = userJSONObject.getString(USER_ID);
                this.userName = userJSONObject.getString(USER_NAME);
            }
            catch (Exception e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public interface Callback
    {
        void onUserProfileReceived(FacebookUserData userData);
        void onUserFriendsReceived(List<FacebookUserData> userDataList);
    }
}
