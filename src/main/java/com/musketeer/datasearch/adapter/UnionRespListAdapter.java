package com.musketeer.datasearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.ScreenUtils;
import com.musketeer.baselibrary.util.StringUtils;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.util.ImageLoader;
import com.musketeer.datasearch.util.Net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-12.
 */
public class UnionRespListAdapter extends BaseRecyclerAdapter<UnionResultResp, UnionRespListViewHolder> {

    public UnionRespListAdapter(Context context) {
        super(context);
    }

    public UnionRespListAdapter(Context context, List<UnionResultResp> list) {
        super(context, list);
    }

    @Override
    public UnionRespListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_union_search_list, null);
        return new UnionRespListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UnionRespListViewHolder holder, int position) {
        mDataList.get(position).setClickListener(clickListener);
        mDataList.get(position).setDeleteListener(deleteListener);
        holder.bindView(mContext, mDataList.get(position));
    }
}

class UnionRespListViewHolder extends RecyclerView.ViewHolder {
    TextView mSearchKeywords;
    ImageView mDetail;
    ImageView mDelete;
    LinearLayout mWebList;
    HorizontalScrollView mWebListRoot;

    public UnionRespListViewHolder(View itemView) {
        super(itemView);
        mSearchKeywords = (TextView) itemView.findViewById(R.id.union_search_keywords);
        mDetail = (ImageView) itemView.findViewById(R.id.detail);
        mDelete = (ImageView) itemView.findViewById(R.id.delete);
        mWebList = (LinearLayout) itemView.findViewById(R.id.web_list);
        mWebListRoot = (HorizontalScrollView) itemView.findViewById(R.id.web_list_root);
    }

    public void bindView(Context context, UnionResultResp item) {
        itemView.setTag(item);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnionResultResp item = (UnionResultResp) v.getTag();
                if (item.getClickListener() == null) return;
                item.getClickListener().onClick(item);
            }
        });
        mDetail.setTag(item);
        mDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnionResultResp item = (UnionResultResp) v.getTag();
                if (item.getClickListener() == null) return;
                item.getClickListener().onClick(item);
            }
        });
        mSearchKeywords.setText(item.getKeyword().replace("+", " "));
        if (item.isNew()) {
            mSearchKeywords.setTextColor(context.getResources().getColor(R.color.app_theme_color));
        } else {
            mSearchKeywords.setTextColor(context.getResources().getColor(R.color.text_color));
        }
        mDelete.setTag(item);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnionResultResp item = (UnionResultResp) v.getTag();
                if (item.getDeleteListener() == null) return;
                item.getDeleteListener().onItemDelete(item);
            }
        });
        mWebList.removeAllViews();
        List<WebEntity> webList=item.getWebEntitys();
        for (WebEntity web:webList) {
            View view=LayoutInflater.from(context).inflate(R.layout.include_web_item, null);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) ScreenUtils.dpToPx(context, 10), 0, (int)ScreenUtils.dpToPx(context, 10), 0);
            view.setLayoutParams(params);
            ImageView mLogo=(ImageView) view.findViewById(R.id.logo);
            if (!StringUtils.isEmpty(web.getLogo())) {
                ImageLoader.loadImage(mLogo, Net.getAbsoluteImageUrl(web.getLogo()));
                mLogo.setVisibility(View.VISIBLE);
            } else {
                mLogo.setVisibility(View.GONE);
            }
            TextView mWebName=(TextView) view.findViewById(R.id.web_name);
            mWebName.setText(web.getName());
            mWebList.addView(view);
        }
    }
}