package com.itunessearchandplay.itunessearchandplay.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.itunessearchandplay.itunessearchandplay.R

/**
 * Author: Andreas Pribitzer
 */

class RemoveFromFavoritesDialog: DialogFragment() {

    companion object {
        private const val KEY_POSITION = "position"

        fun newInstance(position: Int): RemoveFromFavoritesDialog {
            val fragment =
                RemoveFromFavoritesDialog()
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private var listener: IRemoveFromFavorites? = null
    fun setListener(listener: IRemoveFromFavorites) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val position = arguments?.getInt(KEY_POSITION)

        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.favorites)
            .setMessage(getString(R.string.remove_from_favorites))
            .setPositiveButton(android.R.string.yes) {_,_->
                if(listener != null && position != null) listener!!.removeFromFavorites(position)
            }
            .setNegativeButton(android.R.string.no) {_,_-> }
        return builder.create()
    }

    interface IRemoveFromFavorites {
        fun removeFromFavorites(position: Int)
    }
}