import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ItemEstateRecyclerBinding
import com.example.realestatemanager.model.EstateModel
import com.openclassrooms.realestatemanager.Utils

class EstateItemAdapter(private val estateList: List<EstateModel>) :
    RecyclerView.Adapter<EstateItemAdapter.EstateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val binding: ItemEstateRecyclerBinding =
            ItemEstateRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EstateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        val estate = estateList[position]
        holder.bind(estate)
    }

    override fun getItemCount(): Int {
        return estateList.size
    }

    inner class EstateViewHolder(private val binding: ItemEstateRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val estatePicture: ImageView = binding.estatePicture
        private val estateType: TextView = binding.estateType
        private val estateCity: TextView = binding.estateCity
        private val estatePrice: TextView = binding.estatePrice

        fun bind(estate: EstateModel) {
            estateType.text = estate.type.label
            estateCity.text = Utils.extractCityFromAddress(estate.address)
            estatePrice.text = "$${estate.dollarPrice}"

            val uri = estate.pictures[0].first
            loadImageWithGlide(uri)
        }

        private fun loadImageWithGlide(uri: Uri) {
            Glide.with(binding.root.context)
                .load(uri)
                .placeholder(R.drawable.ic_gallery_black_24dp)
                .centerCrop()
                .into(estatePicture)
        }
    }
}
