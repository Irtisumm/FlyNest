package com.group.FlyNest.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.group.FlyNest.R

class OfferFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moreOptionsButton: FloatingActionButton
    private val allOffers = mutableListOf<CouponOffer>()
    private lateinit var allCouponAdapter: AllCouponAdapter
    private lateinit var claimedCouponAdapter: ClaimedCouponAdapter
    private lateinit var expiredCouponAdapter: ExpiredCouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.offers_recycler_view)
            ?: throw IllegalStateException("RecyclerView not found")
        moreOptionsButton = view.findViewById(R.id.more_options_button)
            ?: throw IllegalStateException("More options button not found")
    }

    private fun setupRecyclerView() {
        allOffers.clear()
        allOffers.addAll(getSampleOffers())
        allCouponAdapter = AllCouponAdapter(allOffers.filter { it.status == CouponStatus.ALL }) { coupon ->
            Toast.makeText(context, "Redeeming: ${coupon.code}", Toast.LENGTH_SHORT).show()
            coupon.status = CouponStatus.CLAIMED
            updateCouponAdapters()
        }
        claimedCouponAdapter = ClaimedCouponAdapter(allOffers.filter { it.status == CouponStatus.CLAIMED })
        expiredCouponAdapter = ExpiredCouponAdapter(allOffers.filter { it.status == CouponStatus.EXPIRED })
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allCouponAdapter // Default to "All" view
        }
    }

    private fun setupClickListeners() {
        moreOptionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 1, 0, "All Coupons")
        popup.menu.add(0, 2, 1, "Claimed Coupons")
        popup.menu.add(0, 3, 2, "Expired Coupons")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    recyclerView.adapter = allCouponAdapter
                    Toast.makeText(context, "Showing All Coupons", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    recyclerView.adapter = claimedCouponAdapter
                    Toast.makeText(context, "Showing Claimed Coupons", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    recyclerView.adapter = expiredCouponAdapter
                    Toast.makeText(context, "Showing Expired Coupons", Toast.LENGTH_SHORT).show()
                }
                else -> false
            }
            true
        }
        popup.show()
    }

    private fun updateCouponAdapters() {
        allCouponAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.ALL })
        claimedCouponAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.CLAIMED })
        expiredCouponAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.EXPIRED })
    }

    private fun getSampleOffers(): List<CouponOffer> {
        return listOf(
            // Existing coupon with ALL status
            CouponOffer(
                "20% Off Flights",
                "Valid until May 31, 2025",
                null,
                R.drawable.percent_discount,
                "FLY20",
                CouponType.PERCENTAGE_DISCOUNT,
                CouponStatus.ALL
            ),
            // New coupons with ALL status
            CouponOffer(
                "15% Off Car Rentals",
                "Valid until June 15, 2025",
                null,
                R.drawable.percent_discount,
                "CAR15",
                CouponType.PERCENTAGE_DISCOUNT,
                CouponStatus.ALL
            ),
            CouponOffer(
                "$25 Off Tours",
                "Book by May 20, 2025",
                null,
                R.drawable.fixed_discount,
                "TOUR25",
                CouponType.FIXED_AMOUNT_DISCOUNT,
                CouponStatus.ALL
            ),
            CouponOffer(
                "Free Luggage Tag",
                "With any booking over $100",
                "https://example.com/luggage.jpg",
                null,
                "TAG100",
                CouponType.FREE_ITEM,
                CouponStatus.ALL
            ),
            // Existing coupon with CLAIMED status
            CouponOffer(
                "$50 Off Hotels",
                "Book by April 30, 2025",
                null,
                R.drawable.fixed_discount,
                "HOTEL50",
                CouponType.FIXED_AMOUNT_DISCOUNT,
                CouponStatus.CLAIMED
            ),
            // Existing coupon with EXPIRED status
            CouponOffer(
                "Free Airport Transfer",
                "For bookings over $200",
                "https://example.com/transfer.jpg",
                null,
                "FREE200",
                CouponType.FREE_ITEM,
                CouponStatus.EXPIRED
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = OfferFragment()
    }
}

data class CouponOffer(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val imageResId: Int?,
    val code: String,
    val type: CouponType,
    var status: CouponStatus = CouponStatus.ALL
)

enum class CouponType { PERCENTAGE_DISCOUNT, FIXED_AMOUNT_DISCOUNT, FREE_ITEM }
enum class CouponStatus { ALL, CLAIMED, EXPIRED }