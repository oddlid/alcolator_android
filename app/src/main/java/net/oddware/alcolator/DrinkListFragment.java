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
import com.sortabletableview.recyclerview.toolkit.SimpleTableHeaderAdapter;

import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

/*
2020-10-05:
Since the SortableTableView lib is written in Java, there are some incompatabilities
with Kotlin, especially for methods that accept a Comparator<T>, DataClickListener<T>
and probably other stuff with generics. When I try to pass instances of my subclasses,
I get "expected Nothing, got xxx".
O I try to write this class in Java, because I'm tired of googling and not finding any
good answers.
 */
public class DrinkListFragment extends Fragment {
    private SortableTableView<Drink> mTblDrinks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drink_list, container, false);
        mTblDrinks = view.findViewById(R.id.tblDrinks);
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
                                getContext(),
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
                                getContext(),
                                AddDrinkActivity.CFG_ACTION_ADD,
                                0
                        ),
                        AddDrinkActivity.CFG_ACTION_ADD
                );
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
}
