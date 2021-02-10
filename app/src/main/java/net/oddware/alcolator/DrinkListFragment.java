package net.oddware.alcolator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.sortabletableview.recyclerview.SortableTableView;
import com.sortabletableview.recyclerview.model.TableColumnWeightModel;
import com.sortabletableview.recyclerview.toolkit.FilterHelper;
import com.sortabletableview.recyclerview.toolkit.SimpleTableHeaderAdapter;

import net.oddware.alcolator.databinding.FragmentDrinkListBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;

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
public class DrinkListFragment extends Fragment {
    private SortableTableView<Drink> mTblDrinks;
    //private TagListFragment mTagListFrag;
    private FilterHelper<Drink> mFilterHelper;
    private FragmentDrinkListBinding binding;
    private DrinkViewModel drinkViewModel;
    private DrinkTableAdapter drinkTableAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_drink_list, container, false);
        binding = FragmentDrinkListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //mTblDrinks = view.findViewById(R.id.tblDrinks);
        mTblDrinks = binding.tblDrinks;
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

        //mTblDrinks.addDataClickListener(new TableDataClickListener<Drink>() {
        //    @Override
        //    public void onDataClicked(int rowIndex, Drink drink) {
        //        startActivityForResult(
        //                DrinkDetailActivity.getLaunchIntent(
        //                        requireContext(),
        //                        drink.getId()
        //                ),
        //                DrinkDetailActivity.DRINK_ACTION_VIEW
        //        );
        //    }
        //});
        mTblDrinks.addDataClickListener((rowIndex, drink) -> {
            DrinkListFragmentDirections.ActionDrinkListFragmentToDrinkDetailFragment action
                    = DrinkListFragmentDirections.actionDrinkListFragmentToDrinkDetailFragment();
            action.setDrinkID(drink.getId());
            Navigation.findNavController(view).navigate(action);
        });

        //view.findViewById(R.id.fabAddDrink).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        startActivityForResult(
        //                AddDrinkActivity.getLaunchIntent(
        //                        requireContext(),
        //                        AddDrinkActivity.CFG_ACTION_ADD,
        //                        0
        //                ),
        //                AddDrinkActivity.CFG_ACTION_ADD
        //        );
        //    }
        //});
        binding.fabAddDrink.setOnClickListener(v -> {
            DrinkListFragmentDirections.ActionDrinkListFragmentToEditDrinkFragment action
                    = DrinkListFragmentDirections.actionDrinkListFragmentToEditDrinkFragment();
            action.setLoadAction(EditDrinkFragment.ACTION_ADD);
            Navigation.findNavController(view).navigate(action);
        });

        //view.findViewById(R.id.fabFilter).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        if (null == mTagListFrag) {
        //            mTagListFrag = new TagListFragment(DrinkListFragment.this);
        //        }
        //        mTagListFrag.show(getParentFragmentManager(), "TagListFragment");
        //    }
        //});

        // This only works once. Filter is not reset, and we get nothing after changing the tag a second or so time...
        // This seems to in some way be caused by the new navigation system, as this fragment is reloaded
        // after interacting with the tag selection dialog, and so, we always start with null instances
        // of class members, like adapter, filterhelper etc.
        // The most hard thing to understand though, and probably the main cause of all problems, is
        // that the list of drinks from DrinkViewModel is modified somewhere, so we lose more and more
        // of the list after each filter, until it's empty.
        // Update:
        // If we first filter, and then either add a new drink, or delete one of those shown, then the
        // logs show that we first get a list of drinks with the filtered length (before add/remove), then right after a
        // list with the unfiltered length, and then, if we navigate in some way and come back to this
        // fragment, THEN the full list is back! Buggy as fuck...
        //setFilter("");
        boolean disableFiltering = true;
        binding.fabFilter.setOnClickListener(v -> {
            // Just skipping this whole thing until we can figure it out
            if (disableFiltering) {
                Toast.makeText(requireContext(), "Filtering does not work yet", Toast.LENGTH_LONG).show();
                return;
            }

            NavController navCtl = Navigation.findNavController(v);
            NavDirections action = DrinkListFragmentDirections.actionDrinkListFragmentToTagListFragment();
            NavBackStackEntry nsbe = navCtl.getCurrentBackStackEntry();
            if (null == nsbe) {
                return;
            }
            SavedStateHandle ssh = nsbe.getSavedStateHandle();
            // So it seems all the problems we have below, is related to the fact that we leave this
            // fragment when showing the Tag Selection Dialog. So onCreateView gets called again,
            // and our instance vars like adapter, tableview and filter gets reset. But, somehow,
            // the filter that modifies the drinkList is still in effect, so the list we get back on
            // recreation is filtered to the previous selection. And hence we lose the original list...
            ssh.getLiveData(TagListFragment.FILTER_TAG_KEY)
                    .observe(getViewLifecycleOwner(), val -> {
                        // It seems after reading the source of SortableTableView that the FilterHelper
                        // will actually modify the contents of the tableView's adapter. That's so fucking "smart"...
                        // So, if we are to get filtering working, we'd need to reset the adapter as well as the filter,
                        // in the right order, or nothing works at all.
                        // Man, if I could just take the license money I paid the developer of
                        // SortableTableView and beat him in the head with it...
                        // Well, tried resetting the adapter as well, and even that doesn't work :(
                        // Nothing seems to work for setting the filter. It just can't fucking work
                        // It works the first time, but never after, in any way...
                        // The list just gets cleared, and you need to restart the app to get it back.
                        Timber.d("Fab filter listened and got: '%s'", val);
                        String tag = val.toString();
                        if (null == tag || tag.isEmpty()) {
                            Timber.d("Got empty tag, resetting...");
                            if (null != mFilterHelper) { // we never reach this branch, for some reason
                                Timber.d("mFilterHelper is NOT null, calling clearFilter()...");
                                mFilterHelper.clearFilter();
                                //mFilterHelper = null;
                                // Doesn't work either
                                //drinkViewModel.getDrinks().observe(getViewLifecycleOwner(), drinks -> {
                                //    mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), drinks));
                                //});
                            } else { // we always get here
                                // Setting a new, empty filter doesn't make any difference, as the problem
                                // we have is that the actual list from DrinkViewModel is already filtered from last call
                                Timber.d("mFilterHelper is NULL, so we can't call clearFilter()");
                                //Timber.d("mFilterHelper is NULL, creating new instance...");
                                //mFilterHelper = new FilterHelper<>(mTblDrinks);
                                //Timber.d("Setting new TagFilter with tag '%s'", tag);
                                //mFilterHelper.setFilter(new TagFilter(tag));
                                setFilter(tag);
                            }
                        } else {
                            Timber.d("Got '%s' for tag, setting filter...", tag);
                            //mFilterHelper = new FilterHelper<>(mTblDrinks);
                            // Doesn't work either
                            //drinkViewModel.getDrinks().observe(getViewLifecycleOwner(), drinks -> {
                            //    mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), drinks));
                            //});
                            //if (null == mFilterHelper) { // this is always the case
                            //    Timber.d("mFilterHelper is NULL, creating new instance...");
                            //    mFilterHelper = new FilterHelper<>(mTblDrinks);
                            //}
                            ////mFilterHelper.clearFilter();
                            //Timber.d("Setting new TagFilter with tag '%s'", tag);
                            //mFilterHelper.setFilter(new TagFilter(tag));
                            setFilter(tag);
                        }
                        ssh.remove(TagListFragment.FILTER_TAG_KEY);
                    });

            navCtl.navigate(action);
        });

        //if (null != mFilterHelper) {
        //    mFilterHelper = null;
        //}

        return view;
    }

    private void setFilter(String tag) {
        if (null == mFilterHelper) {
            Timber.d("mFilterHelper is NULL, creating new instance and setting");
            mFilterHelper = new FilterHelper<>(mTblDrinks);
        }
        Timber.d("Setting filter: '%s'", tag);
        mFilterHelper.setFilter(new TagFilter(tag));
    }

    /*
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

     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //mFilterHelper = null;
        //mTblDrinks = null;
        //drinkTableAdapter = null;
        //// In the logs, this comes _after_ we have loaded the tag selection dialog and confirmed or
        //// cancelled it and returned, _after_ the messages from onCreateView. Crap...
        //Timber.d("Destroyed fragment instance data");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drinkViewModel = new ViewModelProvider(this).get(DrinkViewModel.class);

        // This triggers every time we do filtering, and the returned size is after filter applied.
        // After filtering the first time, the list is reduced to what's matching the tag, as it's supposed to.
        // But when filtering the next time, even when resetting the filter, it's filtered against the
        // the previously filtered list, so an empty list is returned, as the tag doesn't match.
        // How to get back the original list, beats me at the moment....
        // The log statement that happens _before_ setting a new adapter, even shows that the list is
        // reduced, and I can't figure out how that happens, as the livedata is not supposed to be
        // mutable in that direction...
        // Actually, it doesn't even work to navigate to other fragments in the app and come back to this one.
        // The list is still not restored, but remains gone...
        // Update:
        // After another day of debugging, we now see that if we create the FilterHelper here, or in
        // the start of onCreateView (outside click listeners), then the detection for mFilterHelper is NULL
        // always register as false, as the object exists. And if so, calling clearFilter or setFilter
        // doesn't do anything - it just returns the full list.
        // On the other hand, if we create mFilterHelper on demand in a click listener, then the check
        // for mFilterHelper is NULL is always true, no matter how many times we click, and so the FilterHelper
        // is always recreated, which leads to the list getting empty after 2 filterings.
        drinkViewModel.getDrinks().observe(getViewLifecycleOwner(), drinks -> {
            if (null != drinks) {
                //Timber.d("Got a list of drinks (length: %s). Setting new Table Adapter", drinks.size());
                //if (null == drinkTableAdapter) { // so, this actually seems to happen every time...?
                //    Timber.d("Creating new DrinkTableAdapter with %d drinks", drinks.size());
                //    drinkTableAdapter = new DrinkTableAdapter(requireContext(), drinks);
                //} else {
                //    // We get here if we add or remove a drink
                //    Timber.d("adapter is NOT NULL, so we should just pass it a new list");
                //}
                ////mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), drinks));
                //mTblDrinks.setDataAdapter(drinkTableAdapter);
                Timber.d("Creating and setting new DrinkTableAdapter with %d drinks", drinks.size());
                // So, this is interesting; When we wrap drinks in a new ArrayList when passing to a new adapter
                // instance, we first get a filtered list in the UI like we want, but like 1 second after,
                // we get to this same code again with the unfiltered list, and it's reset to no filter.
                mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), new ArrayList<>(drinks)));
                //mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), Collections.synchronizedList(new ArrayList<>(drinks))));
                //mTblDrinks.setDataAdapter(new DrinkTableAdapter(requireContext(), drinks));
                //setFilter("");
            }
        });

        // Let's try to hide the keyboard from here, as it didn't work well in the containing activity
        // Yes! This works well!
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
    public int getListCount() {
        DrinkTableAdapter dta = (DrinkTableAdapter) mTblDrinks.getDataAdapter();
        if (null == dta) {
            return 0;
        }
        return dta.getItemCount();
    }

     */


    //@Override
    //public void onResume() {
    //    super.onResume();
    //    mFilterHelper = new FilterHelper<>(mTblDrinks);
    //}

    /*
    @Override
    public void onCancelTag() {
        Timber.d("Tag selection cancelled");
        if (null != mTagListFrag) {
            mTagListFrag.dismiss();
        }
    }

     */

    /*
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

     */

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
            if (query.trim().isEmpty()) {
                Timber.d("Empty filter accepts all");
                return true; // no filter should just accept any tag without even checking
            }
            final String lcTag = data.getTag().toLowerCase();
            final String lcQuery = query.toLowerCase();
            boolean has = lcTag.contains(lcQuery);
            //Timber.d("Tag contains %s: %b", lcQuery, has);
            Timber.d("%s == %s = %b", lcTag, lcQuery, has);
            return has;
        }
    }
}
