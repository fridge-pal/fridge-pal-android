package cp317.wlu.ca.fridgepal.fridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;

import cp317.wlu.ca.fridgepal.R;
import cp317.wlu.ca.fridgepal.model.Category;
import cp317.wlu.ca.fridgepal.model.Food;
import cp317.wlu.ca.fridgepal.repositories.FoodRepository;

public class FridgeListFragment extends Fragment {
    private ItemAdapter mAdapter;
    private FoodRepository foodRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodRepository = FoodRepository.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fridge_list_fragment_layout, container, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new ItemAdapter(foodRepository.getCategories());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton addButton = view.findViewById(R.id.add_food_item_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
            startActivity(intent);
        });

        foodRepository.addDataLoadedListener(() -> {
            mAdapter.setCategories(foodRepository.getCategories());
        });

        return view;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private RecyclerView foodRecyclerView;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fridge_list_item_layout, parent, false));
            categoryName = itemView.findViewById(R.id.category_name);
            foodRecyclerView = itemView.findViewById(R.id.food_recycler_view);
        }
    }

    private class ItemHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView foodName;
        private TextView foodExpDate;
        private Food foodObj;

        public ItemHolder2(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fridge_list_item_layout_2, parent, false));
            itemView.setOnClickListener(this);
            foodName = itemView.findViewById(R.id.food_name);
            foodExpDate = itemView.findViewById(R.id.food_exp_date);

        }

        @Override
        public void onClick(View view) {
            Intent intent = FoodActivity.newIntent(getActivity(), foodObj);
            startActivity(intent);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private ArrayList<Category> mCategories;

        public ItemAdapter(ArrayList<Category> tempCategories) {
            mCategories = tempCategories;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ItemHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            String s = mCategories.get(position).getCategoryName();
            holder.categoryName.setText(s);
            ItemAdapter2 adapter2 = new ItemAdapter2(mCategories.get(position).getFoods());
            holder.foodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            holder.foodRecyclerView.setAdapter(adapter2);
        }

        public void setCategories(ArrayList<Category> categories) {
            this.mCategories = categories;
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }
    }

    private class ItemAdapter2 extends RecyclerView.Adapter<ItemHolder2> {
        private ArrayList<Food> mFoods;

        public ItemAdapter2(ArrayList<Food> tempFoods) {
            mFoods = tempFoods;
        }

        @Override
        public ItemHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ItemHolder2(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder2 holder, int position) {
            LocalDate now = LocalDate.now();
            Food food = mFoods.get(position);
            LocalDate expiryDate = food.getExpiryDateAsDate();

            if (now.until(expiryDate).getDays() < 3) {
                holder.foodExpDate.setTextColor(getResources().getColor(R.color.colorLessThanThree, null));
            } else if (now.until(expiryDate).getDays() < 5) {
                holder.foodExpDate.setTextColor(getResources().getColor(R.color.colorLessThanFive, null));
            }

            holder.foodObj = food;
            holder.foodName.setText(food.getName());
            holder.foodExpDate.setText(food.getExpiryDate());
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }
}