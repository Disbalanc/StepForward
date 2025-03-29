import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stepforward.R
import com.example.stepforward.data.model.Teacher

class TeacherAdapter(private val items: List<Teacher>, private val onItemClick: (Teacher) -> Unit) :
    RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_image)
        val name: TextView = view.findViewById(R.id.item_name)
        val direction: TextView = view.findViewById(R.id.item_direction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.teacher_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teacher = items[position]
        holder.image.setImageResource(teacher.imageRes)
        holder.name.text = teacher.name
        holder.direction.text = teacher.direction.joinToString(", ")

        // Обработка нажатия на элемент
        holder.itemView.setOnClickListener {
            onItemClick(teacher)
        }
    }

    override fun getItemCount() = items.size
}