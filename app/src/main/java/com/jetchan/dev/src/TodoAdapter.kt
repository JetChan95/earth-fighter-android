import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jetchan.dev.R

class TodoAdapter(private var todoList: MutableList<Pair<String, Boolean>>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_todo)
        val textView: TextView = itemView.findViewById(R.id.tv_todo_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position].first
        holder.textView.text = todo
        holder.checkBox.isChecked = todoList[position].second
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateData(newList: MutableList<Pair<String, Boolean>>) {
        todoList.clear()
        todoList.addAll(newList)
        notifyDataSetChanged()
    }
}