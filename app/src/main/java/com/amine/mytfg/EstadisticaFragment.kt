import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amine.mytfg.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class EstadisticaFragment : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estadistica, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()
        loadPieChartData()
    }

    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Hábitos Completados"
        pieChart.setCenterTextSize(24f)
        pieChart.description.isEnabled = false

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }

    private fun loadPieChartData() {
        val repo = HabitoRepository(requireContext())
        repo.contarHabitosCompletados { completados, noCompletados ->
            val entries = ArrayList<PieEntry>().apply {
                add(PieEntry(completados.toFloat(), "Completados"))
                add(PieEntry(noCompletados.toFloat(), "No Completados"))
            }

            val dataSet = PieDataSet(entries, "Hábitos")
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
            dataSet.valueTextSize = 12f
            dataSet.sliceSpace = 3f

            val data = PieData(dataSet)
            activity?.runOnUiThread {
                pieChart.data = data
                pieChart.invalidate()  // refresh
            }
        }
    }

}
