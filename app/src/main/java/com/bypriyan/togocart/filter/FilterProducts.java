package com.bypriyan.togocart.filter;

import android.widget.Filter;


import com.bypriyan.togocart.adapter.AdapterProductsGrid;
import com.bypriyan.togocart.models.ModelProducts;

import java.util.ArrayList;

public class FilterProducts extends Filter {

    private AdapterProductsGrid adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProducts(AdapterProductsGrid adapter, ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelProducts> filteredModel = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getProductBrand().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getProductName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getProductDescription().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getProductType().toUpperCase().contains(charSequence)){

                    filteredModel.add(filterList.get(i));
                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;

        }else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    adapter.productsArrayList = (ArrayList<ModelProducts>)results.values;
    adapter.notifyDataSetChanged();
    }
}
