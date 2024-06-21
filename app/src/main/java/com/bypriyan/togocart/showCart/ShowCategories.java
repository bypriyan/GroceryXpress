package com.bypriyan.togocart.showCart;

import com.bypriyan.togocart.models.ModelCategories;

import java.util.ArrayList;

public class ShowCategories {

    private ArrayList<ModelCategories> categoriesArrayList;

    public ShowCategories(ArrayList<ModelCategories> categoriesArrayList) {
        this.categoriesArrayList = categoriesArrayList;
    }

    public ArrayList<ModelCategories> getCategoriesArraylist(){
        categoriesArrayList.clear();
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Faata%20dal.png?alt=media&token=0ff488d4-c972-43e4-9b0d-ee5d674dc8e1",
                "Atta, Rice & Dal"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fmsalaoil.png?alt=media&token=c743c4b3-c3c2-455b-a129-f60ee0b9fb7b",
                "Masala, Oil & More"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Ftea%20coffie.png?alt=media&token=a398e182-2baf-4ab1-b5d3-fb83118cd082",
                "Tea, Coffee & Health Drink"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fbreakfast.png?alt=media&token=fef1755e-db5f-4fca-ab32-0ff6a341637d",
                "Breakfast & Instant Food"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fdairy%20bread.png?alt=media&token=31977ac8-6f6b-49f6-b8d1-cef30a92456c",
                "Dairy, Bread & Eggs"));

        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fmunchies.png?alt=media&token=42b6fedb-0fc9-4353-a252-34458865fc2e",
                "Snacks & Munchies"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fbakery.png?alt=media&token=d255d87c-9dc0-4d39-95c4-0cd0576f2258",
                "Bakery & Biscuits"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fsweet%20tooth.png?alt=media&token=0cacadd3-f0f3-4638-946a-bf83c6071b46",
                "Sweet Tooth"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fdrinks.png?alt=media&token=e524268d-1b2d-4251-b31f-8ed0fc4a9837",
                "Cold Drinks & Juices"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fsauce.png?alt=media&token=440c3ff8-a537-48c7-8c62-aceca81d6645",
                "Sauces & Spreads"));

        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fbabycare.png?alt=media&token=6e070f34-3918-4021-a2d7-3b95212e2a26",
                "Baby Care"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fhome%20office.png?alt=media&token=aac09db0-22cd-4112-be2c-e8ef68b5abba",
                "Home & Office"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fcleaning.png?alt=media&token=e0f44ca4-d355-436a-b2b4-09ab20f95b87",
                "Cleaning Essentials"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fpersonal%20care.png?alt=media&token=324a8fda-a643-4bff-a9d6-11f7f5e1a3d5",
                "Personal Care"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fdry%20fruits.png?alt=media&token=6298cee0-45d6-4761-8ab1-e26f17f7ef38",
                "Dry Fruits & More"));
        categoriesArrayList.add(new ModelCategories("https://firebasestorage.googleapis.com/v0/b/chhaatr-sangh.appspot.com/o/Postes%2Fpooja%20needs.png?alt=media&token=235739ea-ba9e-430d-85c0-4581b5fb9d9b",
                "Pooja Needs"));

        return categoriesArrayList;
    }

//    public ArrayList<ModelCategDraw> getDrawCategory(){
//        categoriesArrayList.clear();
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.aata_dal,"Atta, Rice & Dal"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.msalaoil,"Masala, Oil & More"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.tea_coffie,"Tea, Coffee & Health Drink"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.breakfast,"Breakfast & Instant Food"));
//
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.dairy_bread,"Dairy, Bread & Eggs"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.munchies,"Snacks & Munchies"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.bakery,"Bakery & Biscuits"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.sweet_tooth,"Sweet Tooth"));
//
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.drinks,"Cold Drinks & Juices"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.sauce,"Sauces & Spreads"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.babycare,"Baby Care"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.home_office,"Home & Office"));
//
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.cleaning,"Cleaning Essentials"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.personal_care,"Personal Care"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.dry_fruits,"Dry Fruits & More"));
//        categoriesArrayList.add(new ModelCategDraw(R.drawable.pooja_needs,"Pooja Needs"));
//        return categoriesArrayList;
//    }

}
