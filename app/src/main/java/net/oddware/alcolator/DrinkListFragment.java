package net.oddware.alcolator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sortabletableview.recyclerview.SortableTableView;
import com.sortabletableview.recyclerview.listeners.TableDataClickListener;
import com.sortabletableview.recyclerview.model.TableColumnWeightModel;
import com.sortabletableview.recyclerview.toolkit.FilterHelper;
import com.sortabletableview.recyclerview.toolkit.SimpleTableHeaderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

/*
2020-10-05:
Since the SortableTableView lib is written in Java, there are some incompatabilities
with Kotlin, especially for methods that accept a Comparator<T>, DataClickListener<T>
and probably other stuff with generics. When I try to pass instances of my subclasses,
I get "expected Nothing, got xxx".
So I try to write this class in Java, because I'm tired of googling and not finding any
good answers.
 */
public class DrinkListFragment extends Fragment implements TagListFragment.TagSelectionListener {
    private SortableTableView<Drink> mTblDrinks;
    private TagListFragment mTagListFrag;
    private FilterHelper<Drink> mFilterHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drink_list, container, false);
        mTblDrinks = view.findViewById(R.id.tblDrinks);
        //mFilterHelper = new FilterHelper<>(mTblDrinks); // creating it here lead to filtering not working
        String[] tblHdrs = getResources().getStringArray(R.array.colHdrs);

        mTblDrinks.setColumnCount(tblHdrs.length);
        mTblDrinks.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(), tblHdrs));


        TableColumnWeightModel tcwm = new TableColumnWeightModel(tblHdrs.length);
        tcwm.setColumnWeight(0, 2); // name
        tcwm.setColumnWeight(1, 1); // volume
        tcwm.setColumnWeight(2, 1); // price
        tcwm.setColumnWeight(3, 1); // percentage
        tcwm.setColumnWeight(4, 1); // ml alcohol
        tcwm.setColumnWeight(5, 1); // price per ml alcohol
        mTblDrinks.setColumnModel(tcwm);

        //mTblDrinks.registerHorizontalScrollView((HorizontalScrollView) view.findViewById(R.id.hsvDrinks));
        //TableColumnDpWidthModel tcdpwm = new TableColumnDpWidthModel(getContext(), 6, 96);
        //tcdpwm.setColumnWidth(0, 192);
        //mTblDrinks.setColumnModel(tcdpwm);

        mTblDrinks.setColumnComparator(0, new NameComparator());
        mTblDrinks.setColumnComparator(1, new VolumeComparator());
        mTblDrinks.setColumnComparator(2, new PriceComparator());
        mTblDrinks.setColumnComparator(3, new PercentComparator());
        mTblDrinks.setColumnComparator(4, new AlcVolComparator());
        mTblDrinks.setColumnComparator(5, new APKComparator());

        mTblDrinks.addDataClickListener(new TableDataClickListener<Drink>() {
            @Override
            public void onDataClicked(int rowIndex, Drink drink) {
                startActivityForResult(
                        DrinkDetailActivity.getLaunchIntent(
                                Objects.requireNonNull(getContext()),
                                drink.getId()
                        ),
                        DrinkDetailActivity.DRINK_ACTION_VIEW
                );
            }
        });

        view.findViewById(R.id.fabAddDrink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AddDrinkActivity.getLaunchIntent(
                                Objects.requireNonNull(getContext()),
                                AddDrinkActivity.CFG_ACTION_ADD,
                                0
                        ),
                        AddDrinkActivity.CFG_ACTION_ADD
                );
            }
        });

        view.findViewById(R.id.fabFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mTagListFrag) {
                    mTagListFrag = new TagListFragment(DrinkListFragment.this);
                }
                mTagListFrag.show(getParentFragmentManager(), "TagListFragment");
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DrinkViewModel dvm = new ViewModelProvider(this).get(DrinkViewModel.class);

        dvm.getDrinks().observe(getViewLifecycleOwner(), new Observer<List<Drink>>() {
            @Override
            public void onChanged(List<Drink> drinks) {
                if (null != drinks) {
                    Timber.d("Got a list of drinks. Setting new Table Adapter");
                    mTblDrinks.setDataAdapter(new DrinkTableAdapter(getContext(), drinks));
                }
            }
        });
    }

    public int getListCount() {
        DrinkTableAdapter dta = (DrinkTableAdapter) mTblDrinks.getDataAdapter();
        if (null == dta) {
            return 0;
        }
        return dta.getItemCount();
    }

    @Override
    public void onSelectTag(@NotNull String tag) {
        Timber.d("Selected tag: %s", tag);
        if (null != mTagListFrag) {
            mTagListFrag.dismiss();
            // creating the filterhelper here on demand, was key to making it work
            if (null == mFilterHelper) {
                mFilterHelper = new FilterHelper<>(mTblDrinks);
            }
            mFilterHelper.setFilter(new TagFilter(tag));
        } else {
            Timber.d("tagListFrag is null");
        }
    }

    @Override
    public void onCancelTag() {
        Timber.d("Tag selection cancelled");
        if (null != mTagListFrag) {
            mTagListFrag.dismiss();
        }
    }

    @Override
    public void onResetTag() {
        Timber.d("Tag selection reset");
        if (null != mTagListFrag) {
            mTagListFrag.dismiss();
            if (null != mFilterHelper) {
                mFilterHelper.clearFilter();
            }
        }
    }

    private static class NameComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return d1.getName().compareTo(d2.getName());
        }
    }

    private static class VolumeComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return Integer.compare(d1.getVolumeML(), d2.getVolumeML());
        }
    }

    //private static class DrinkClickListener implements TableDataClickListener<Drink> {
    //    @Override
    //    public void onDataClicked(int rowIndex, Drink clickedData) {
    //
    //    }
    //}

    private static class PriceComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return Double.compare(d1.getPrice(), d2.getPrice());
        }
    }

    private static class PercentComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return Double.compare(d1.getAlcPct(), d2.getAlcPct());
        }
    }

    private static class AlcVolComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return Double.compare(d1.alcML(), d2.alcML());
        }
    }

    private static class APKComparator implements Comparator<Drink> {
        @Override
        public int compare(Drink d1, Drink d2) {
            return Double.compare(d1.pricePerAlcML(), d2.pricePerAlcML());
        }
    }

    private static final class TagFilter implements FilterHelper.Filter<Drink> {
        private final String query;

        TagFilter(final String query) {
            this.query = query;
        }

        @Override
        public boolean apply(@NotNull final Drink data) {
            Timber.d("Tag: %s", data.getTag());
            final String lcTag = data.getTag().toLowerCase();
            final String lcQuery = query.toLowerCase();
            boolean has = lcTag.contains(lcQuery);
            Timber.d("Tag contains %s: %b", lcQuery, has);
            return has;
        }
    }
}
