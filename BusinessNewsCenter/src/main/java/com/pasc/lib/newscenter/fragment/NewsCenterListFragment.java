package com.pasc.lib.newscenter.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.lib.base.fragment.BaseFragment;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.net.resp.BaseRespObserver;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.customview.BaseCustomAdapter;
import com.pasc.lib.newscenter.customview.CustomNewsListByColumnType;
import com.pasc.lib.newscenter.data.NewsCenterConstant;
import com.pasc.lib.newscenter.data.NewsDataManager;
import com.pasc.lib.newscenter.util.NewsCenterUtils;
import com.pasc.lib.widget.dialog.loading.LoadingDialogFragment;
import com.pasc.lib.widget.swiperefresh.PaSwipeRefreshLayout;
import com.pasc.lib.widget.viewcontainer.ViewContainer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class NewsCenterListFragment extends BaseFragment {

    private Activity mActivity;
    private View mRootView;
    private RecyclerView mComRecyclerview;
    private PaSwipeRefreshLayout mComRefreshLayout;
    private ViewContainer mComViewContainer;
    private View swipeRefreshTipView;
    private View swipeRefreshNetErrorTipView;
    private LinearLayout wrapLayout;
    private RelativeLayout errorLayout;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    protected BaseCustomAdapter baseCustomView;

    private static final int NEED_LOAD = 0;
    private static final int NO_NEED_LOAD = 1;
    private int mStatus = 0; // 切换tab时，是否需要loadData
    private String type;
    private String newsColumnType;
    private BaseQuickAdapter mAdapter;
    private List checkUpdateNumList = new ArrayList();

    private LoadingDialogFragment loadingDialogFragment;

    private static final String NEWS_COLUMN_TYPE = "news_column_type";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            newsColumnType = getArguments().getString(NEWS_COLUMN_TYPE);
        }
        if (TextUtils.isEmpty(newsColumnType)) {
            newsColumnType = "1";
        }

        baseCustomView = new CustomNewsListByColumnType("", newsColumnType);

        //        baseCustomView = new CustomTopNewsListType("");
        //        baseCustomView =  new CustomNewsColumnType("");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = super.onCreateView(inflater, container, savedInstanceState);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUIView();
        initTheEvent();
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        Log.e("tag", "onInit");
    }

    @Override
    protected int layoutResId() {
        return R.layout.newscenter_list_layout_fragment;
    }

    public static NewsCenterListFragment newInstance(String columnType) {
        NewsCenterListFragment fragment = new NewsCenterListFragment();
        Bundle args = new Bundle();
        args.putString(NEWS_COLUMN_TYPE, columnType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    private void initUIView() {

        if (mRootView == null) {
            return;
        }

        mComRecyclerview = mRootView.findViewById(R.id.newscenter_recyclerview);
        mComRefreshLayout = mRootView.findViewById(R.id.newscenter_refresh_layout);
        mComViewContainer = mRootView.findViewById(R.id.newscenter_view_container);

        wrapLayout = mRootView.findViewById(R.id.newscenter_view_container_wrap);
        errorLayout = mRootView.findViewById(R.id.newscenter_list_error_view);

        mComRefreshLayout.setEnabled(baseCustomView.isEnableRefresh());

        mComRefreshLayout.setPullRefreshEnable(true);
        mComRefreshLayout.setPushLoadMoreEnable(false);

        swipeRefreshTipView =
                LayoutInflater.from(getContext()).inflate(R.layout.newscenter_swipe_tip_layout, null);
        swipeRefreshNetErrorTipView = LayoutInflater.from(getContext())
                .inflate(R.layout.newscenter_swipe_net_error_tip_layout, null);

        mComRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = baseCustomView.getAdapter();
        mComRecyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mComRecyclerview);

        if (baseCustomView.isEnableLoadMore()) {
            mAdapter.setEnableLoadMore(true);
            mAdapter.setOnLoadMoreListener(() -> baseCustomView.onLoadMore(mAdapter), mComRecyclerview);
        }

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getData() != null) {
                baseCustomView.onItemClick(adapter, adapter.getData().get(position), view, position,
                        mActivity);
            }
        });

        mRootView.findViewById(R.id.newscenter_view_empty_retry_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewsListData(false);
            }
        });

    }

    public void initData() {
        if (mStatus == NEED_LOAD){
            mStatus = NO_NEED_LOAD;
            loadNewsListData(false);
        }
    }

    protected void initTheEvent() {
        // widget组件库的loadmore有问题
        mComViewContainer.setOnReloadCallback(() -> loadMoreData());

        mComRefreshLayout.setOnPullRefreshListener(new PaSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }

            @Override
            public void onPullEnable(boolean b) {

            }
        });
    }

    private void loadNewsListData(boolean isRefresh) {
        if (NetworkUtils.isNetworkConnected(getContext())) {
            loadDataFromNet(isRefresh, false);
        } else {
            loadDataFromCache(isRefresh);
        }
    }

    /**
     * 刷新列表
     */
    private void refreshData() {
        loadNewsListData(true);
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        loadDataFromNet(false, true);
    }

    private void loadDataFromCache(boolean isRefresh) {
        //处理兼容测试空指针
        if (baseCustomView == null) {
            return;
        }
//    if (!isRefresh && mComViewContainer != null) {
//      mComViewContainer.showLoading();
//    }
        baseCustomView.getCacheData()
                .subscribe(new Consumer<List<?>>() {
                    @Override
                    public void accept(List<?> list) throws Exception {

                        showData(list, isRefresh, true, false);

                        // 无网络提示
                        if (isRefresh && swipeRefreshNetErrorTipView != null) {
                            mComRefreshLayout.setTips(swipeRefreshNetErrorTipView);
                        } else {
                            mComRefreshLayout.setRefreshing(false);
                        }
                        mComRefreshLayout.setLoadMore(false);
                    }
                });
    }

    private void loadDataFromNet(boolean isRefresh, boolean isLoadMore) {
        //处理兼容测试空指针
        if (baseCustomView == null) {
            return;
        }
        // 不现实ViewContainer的loading
//    if (!isRefresh && mComViewContainer != null) {
//      mComViewContainer.showLoading();
//    }
        showLoadingDialog();

        baseCustomView.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRespObserver<List<?>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable.add(disposable);
                    }

                    @Override
                    public void onSuccess(List<?> list) {
                        dismissLoadingDialog();
                        showData(list, isRefresh, false, isLoadMore);

                        int updateNum = 0;
                        if (isRefresh) {
                            // 计算更新新闻条数
                            updateNum = getNewsUpdateNum(list);
                            // 提示新闻更新条数
                            if (updateNum > 0 && swipeRefreshTipView != null) {
                                TextView view = swipeRefreshTipView.findViewById(R.id.newscenter_swipe_refresh_num);
                                String updateTips = getString(R.string.newscenter_swipe_refresh_num_tip);
                                updateTips = String.format(updateTips, updateNum + "");
                                if (view != null) {
                                    view.setText(updateTips);
                                }
                                mComRefreshLayout.setTips(swipeRefreshTipView);
                            }else {
                                mComRefreshLayout.setRefreshing(false);
                            }
                        }

                        // 更新checkUpdateNumList
                        if (!isLoadMore) {
                            updateListData(list);
                        }

                        mComRefreshLayout.setLoadMore(false);
                    }

                    @Override
                    public void onV2Error(String code, String errorMsg) {
                        dismissLoadingDialog();

                        if (!isRefresh) {
                            mComViewContainer.showBlank();
                        } else {
                            // 无网络提示
                            if (swipeRefreshNetErrorTipView != null) {
                                mComRefreshLayout.setTips(swipeRefreshNetErrorTipView);
                            } else {
                                mComRefreshLayout.setRefreshing(false);
                            }
                        }

                        mStatus = NEED_LOAD;
                    }
                });
    }

    private void showData(List listData, boolean isRefresh, boolean isLoadCache, boolean isLoadMore) {
        if (mComViewContainer == null || mAdapter == null) {
            return;
        }

        if (listData == null || listData.isEmpty()) {
            if (!isRefresh && !isLoadMore) {
                if (!NetworkUtils.isNetworkConnected(getContext())) {
                    showNetErrorUI();
                } else {
                    mComViewContainer.showBlank();
                }
                // 当刷新或者首次进入页面为空，切换Tab时重新加载
                mStatus = NEED_LOAD;
            }

            return;
        }

        showDataUI();
        mStatus = NO_NEED_LOAD;
        mAdapter.setNewData(listData);
        mComViewContainer.showContent(R.id.newscenter_refresh_layout);
        if (listData.size() < BaseCustomAdapter.PAGE_SIZE) {
            mAdapter.loadMoreEnd(false);
        } else {
            mAdapter.loadMoreComplete();
        }

        // 不满一页的时候禁用loadMore，不显示 "没有更多数据"的提示
        mAdapter.disableLoadMoreIfNotFullPage();

        // 保存新闻数据
        if (!isLoadCache) {
            NewsDataManager.saveNewsListData(NewsCenterConstant.NewsType.COLUMNNEWS, newsColumnType,
                    "1", BaseCustomAdapter.PAGE_SIZE + "", "", mAdapter.getData());
        }
    }

    /**
     * 显示网络异常页面
     */
    private void showNetErrorUI() {
        if (wrapLayout != null) {
            wrapLayout.setVisibility(View.GONE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示正常页面
     */
    private void showDataUI() {
        if (wrapLayout != null) {
            wrapLayout.setVisibility(View.VISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新checkUpdateNumList
     * 计算更新数量
     * @param list
     */
    private void updateListData(List list) {
        if (checkUpdateNumList == null) {
            return;
        }
        checkUpdateNumList.clear();
        checkUpdateNumList.addAll(NewsCenterUtils.getListStr(list));
    }

    /**
     * 计算新闻更新数量
     *
     * @param latestNewsList 最新的新闻列表
     * @return
     */
    private int getNewsUpdateNum(List latestNewsList) {
        int count = 0;
        if (latestNewsList != null && latestNewsList.size() > 0) {
            if (checkUpdateNumList == null || checkUpdateNumList.isEmpty()) {
                count = latestNewsList.size();
            } else {
                count = NewsCenterUtils.getStrListDiffNum(checkUpdateNumList, NewsCenterUtils.getListStr(latestNewsList));
            }
        }
        return count;
    }

    /**
     * 显示Loading框
     */
    private void showLoadingDialog() {
        String fragmentTag = "newscenterlistfg";
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment preFragment = getFragmentManager().findFragmentByTag(fragmentTag);
        if (preFragment != null) {
            fragmentTransaction.remove(preFragment);
        }

        loadingDialogFragment = new LoadingDialogFragment.Builder()
                .setCancelable(true)
                .build();
        fragmentTransaction.add(loadingDialogFragment, fragmentTag);
        // 避免出现 IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.show(loadingDialogFragment);
    }

    private void dismissLoadingDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismissAllowingStateLoss();
            loadingDialogFragment = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
