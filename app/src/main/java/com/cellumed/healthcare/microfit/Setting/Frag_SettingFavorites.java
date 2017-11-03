package com.cellumed.healthcare.microfit.Setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cellumed.healthcare.microfit.DAO.DAO_Favorites;
import com.cellumed.healthcare.microfit.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.Home.Imp_Click;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;
import com.cellumed.healthcare.microfit.DataBase.SqlImp;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Frag_SettingFavorites extends Fragment implements Imp_Click, SqlImp {

    @Bind(R.id.rv_list)
    RecyclerView rvList;

    private ArrayList<DAO_Favorites> favoritesList;
    private Adapter_FavoritesList adapter;

    public Frag_SettingFavorites() {
        // Required empty public constructor
    }

    public static Frag_SettingFavorites newInstance() {
        return new Frag_SettingFavorites();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_setting_favorites, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        Log.d("Frag_SettingFavorites", "onResume()");
        super.onResume();
        //rvList.setHasFixedSize(true);
        rvList.setHasFixedSize(false);
        rvList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        rvList.setLayoutManager(manager);
        favoritesList = getUserList();
        favoritesSettingData();
    }
    private void favoritesSettingData() {
        adapter = new Adapter_FavoritesList(getContext(), this, favoritesList);
        rvList.setAdapter(adapter);
    }

    private ArrayList<DAO_Favorites> getUserList() {
        final DBQuery dbQuery = new DBQuery(getContext());
        return dbQuery.getFavorites();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

  @Override
    public void checked(int pos) {

      Log.d("Frag_SettingFavorites", "checked pos=" + pos);

      ArrayList<Integer> checkedPos = new ArrayList<>();
      ArrayList<DAO_Favorites> list = adapter.getmDataList();
      final DBQuery dbQuery = new DBQuery(getContext());
      final String userId = ""; //BudUtil.getShareValue(getContext(), BudUtil.USER_ID);

      boolean checked_fav;
      
      // check pos is already in favorite list or not.
      checked_fav=false;
      for (DAO_Favorites favorite : adapter.getmFavList()) {
          if (list.get(pos).getIdx().equals(favorite.getLinkIdx())) {
              checked_fav=true;
              break;
          }
      }
    
      if (checked_fav) {
        // del if already checked
          Log.d("Frag_SettingFavorites", "dbQuery.favoritesRemove():" + dbQuery.favoritesRemove( list.get(pos).getIdx(), userId));
      }
      else
      {
          // new item checked.
          if(adapter.getmFavList().size() < 6)
          {
              Log.d("Frag_SettingFavorites", "getmFavList=" + adapter.getmFavList().size());
                if (dbQuery.getFavoritesCount(userId) < 6) {
                    if (!dbQuery.isFavorites(list.get(pos).getIdx(), userId)) {
                        final HashMap<String, String> favoritesMap = new HashMap<>();
                        favoritesMap.put(FavoritesName, favoritesList.get(pos).getFavoritesName());
                        favoritesMap.put(FavoritesProgram, favoritesList.get(pos).getFavoritesProgram());
                        favoritesMap.put(FavoritesTime, favoritesList.get(pos).getFavoritesTime());
                        favoritesMap.put(FavoritesUserId, userId);
                        favoritesMap.put(FavoritesLinkIdx, favoritesList.get(pos).getIdx());
                        favoritesMap.put(FavoritesType, favoritesList.get(pos).getFavoritesType());
                        favoritesMap.put(FavoritesType2, favoritesList.get(pos).getFavoritesType2());
                        favoritesMap.put(FavoritesState, favoritesList.get(pos).getFavoritesState());
                        favoritesMap.put(FavoritesPosture, favoritesList.get(pos).getFavoritesPosture());
                        favoritesMap.put(FavoritesImageNumber, favoritesList.get(pos).getFavoritesPostureImageNumber());
                        favoritesMap.put(FavoritesTitle, favoritesList.get(pos).getFavoritesTitle());
                        favoritesMap.put(FavoritesRepeatCount, favoritesList.get(pos).getFavoritesRepeatCount());
                        favoritesMap.put(FavoritesStimulusIntensity, favoritesList.get(pos).getFavoritesStimulusIntensity());
                        favoritesMap.put(FavoritesPulseOperationTime, favoritesList.get(pos).getFavoritesPulseOperationTime());
                        favoritesMap.put(FavoritesPulsePauseTime, favoritesList.get(pos).getFavoritesPulsePauseTime());
                        favoritesMap.put(FavoritesFrequency, favoritesList.get(pos).getFavoritesFrequency());
                        favoritesMap.put(FavoritesPulseWidth, favoritesList.get(pos).getFavoritesPulseWidth());
                        favoritesMap.put(FavoritesPulseRiseTime, favoritesList.get(pos).getFavoritesPulseRiseTime());
                        favoritesMap.put(FavoritesExplanation, favoritesList.get(pos).getFavoritesExplanation());
                        favoritesMap.put(FavoritesConstructor, favoritesList.get(pos).getFavoritesConstructor());
                        favoritesMap.put(FavoritesRegDate, favoritesList.get(pos).getFavoritesRegDate());
                        if (dbQuery.newFavoritesInsert(favoritesMap)) {
                            Log.d("Frag_SettingFavorites", "저장 성공");
                        }
                    }
                } else {
                    BudUtil.getInstance().showMaterialDialog(getContext(), getContext().getString(R.string.noAddFabvorites),null);
                    BudUtil.getInstance().showMaterialDialog(getContext(), getContext().getString(R.string.noAddFabvorites),null);
                    //list.get(pos).cb.setChecked(false);

                }
            } else {
                BudUtil.getInstance().showMaterialDialog(getContext(), getContext().getString(R.string.noAddFabvorites),null);
                //list.get(pos).cb.setChecked(false);

            }
        }

        dbQuery.getDb().close();

    }

    @Override
    public void viewClick(int pos) {

    }

}
