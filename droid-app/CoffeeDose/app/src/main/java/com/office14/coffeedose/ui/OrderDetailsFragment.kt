package com.office14.coffeedose.ui


import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffeedose.R
import com.coffeedose.databinding.FragmentOrderBinding
import com.office14.coffeedose.di.InjectingSavedStateViewModelFactory
import com.office14.coffeedose.extensions.setBooleanVisibility
import com.office14.coffeedose.ui.Adapters.OrderDetailsAdapter
import com.office14.coffeedose.ui.Adapters.OrderDetailsItemTouchHelperCallback
import com.office14.coffeedose.ui.Adapters.SwipeListener
import com.office14.coffeedose.viewmodels.OrderDetailsViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_order.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class OrderDetailsFragment : DaggerFragment(), HasDefaultViewModelProviderFactory {

    @Inject
    lateinit var defaultViewModelFactory: InjectingSavedStateViewModelFactory

    //private lateinit var viewModel : OrderDetailsViewModel
    private val viewModel: OrderDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //viewModel  = ViewModelProvider(this, OrderDetailsViewModel.Factory(requireNotNull(this.activity).application)).get(OrderDetailsViewModel::class.java)

        val binding: FragmentOrderBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        initToolbar()

        initOnbackPressedCallBack()

        initRecyclerView(binding.rvOrderDetails)

        handleVisibility()

        initNavigation()

        handleShowError()

        handleLoginRequired()

        handleForceLongPolling()

        handleCommentInput(binding.edComment)

        return binding.root

    }

    private fun handleCommentInput(editText: EditText) {
        editText.doOnTextChanged { text, _, _, _ -> viewModel.comment = text.toString() }
    }


    private fun initOnbackPressedCallBack() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(OrderDetailsFragmentDirections.actionOrderFragmentToDrinksFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun handleVisibility() {
        viewModel.isEmpty.observe(viewLifecycleOwner, Observer {
            rl_content.setBooleanVisibility(!it)
            tv_empty_order_details.setBooleanVisibility(it)
        })

        viewModel.hasOrderInQueue.observe(viewLifecycleOwner, Observer {
            it?.let {
                confirmButton.setBooleanVisibility(!it)
            }

        })
    }

    private fun initNavigation() {
        viewModel.navigateOrderAwaiting.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(OrderDetailsFragmentDirections.actionOrderFragmentToOrderAwaitingFragment())
            viewModel.doneNavigating()
        })
    }

    private fun handleShowError() {
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
                viewModel.hideErrorMessage()
            }
        })
    }


    private fun initRecyclerView(recyclerView: RecyclerView) {
        val rvAdapter = OrderDetailsAdapter()

        val touchCallback = OrderDetailsItemTouchHelperCallback(
            requireContext(),
            rvAdapter,
            SwipeListener { orderDetails -> viewModel.deleteOrderDetailsItem(orderDetails) })

        ItemTouchHelper(touchCallback).attachToRecyclerView(recyclerView)

        val dividerDecor = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)

        with(recyclerView) {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(dividerDecor)
        }

        viewModel.unAttachedOrders.observe(viewLifecycleOwner, Observer {
            rvAdapter.setSource(it)
        })
    }

    private fun initToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
            it.setTitle(R.string.OrderDetails)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_detals_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clearOrderDetails -> {
                viewModel.clearOrderDetails()
                true
            }
            /*R.id.addMore -> {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderFragmentToDrinksFragment())
                true
            }*/
            else -> false
        }
    }

    private fun handleLoginRequired() {
        viewModel.needLogIn.observe(requireActivity(), Observer {
            if (it) {
                (activity as CoffeeDoseActivity).signIn {
                    viewModel.confirmOrder()
                }
                viewModel.doneLogin()
            }
        })
    }

    private fun handleForceLongPolling() {
        viewModel.forceLongPolling.observe(requireActivity(), Observer {
            if (it) {
                (activity as CoffeeDoseActivity).forceLongPolling()
                viewModel.doneForceLongPolling()
            }
        })
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        defaultViewModelFactory.create(this)

}
