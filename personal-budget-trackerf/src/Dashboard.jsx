import { useEffect, useState, useMemo } from 'react'

const MONTH_LABELS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
const PALETTE = ['#6366f1', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#06b6d4', '#84cc16', '#e11d48', '#a855f7']

function BarChart({ months }) {
  const maxVal = Math.max(
    ...months.map((m) => Math.max(m.income, m.expenses)),
    1
  )
  const barWidth = 28
  const gap = 16
  const groupWidth = barWidth * 2 + gap
  const chartHeight = 220
  const chartWidth = months.length * groupWidth + gap
  const ySteps = 5

  return (
    <div className="chart-scroll">
      <svg
        viewBox={`0 0 ${chartWidth + 60} ${chartHeight + 50}`}
        width={chartWidth + 60}
        height={chartHeight + 50}
        className="bar-chart-svg"
      >
        {Array.from({ length: ySteps + 1 }).map((_, i) => {
          const y = (chartHeight / ySteps) * i
          const val = ((maxVal / ySteps) * (ySteps - i)).toFixed(0)
          return (
            <g key={i}>
              <line
                x1="50"
                y1={y + 10}
                x2={chartWidth + 50}
                y2={y + 10}
                stroke="#e2e8f0"
                strokeWidth="1"
              />
              <text
                x="45"
                y={y + 14}
                textAnchor="end"
                fill="#94a3b8"
                fontSize="11"
              >
                {val}
              </text>
            </g>
          )
        })}

        {months.map((m, i) => {
          const x = i * groupWidth + gap + 50
          const incomeH = (m.income / maxVal) * chartHeight
          const expenseH = (m.expenses / maxVal) * chartHeight

          return (
            <g key={m.month}>
              <rect
                x={x}
                y={chartHeight - incomeH + 10}
                width={barWidth}
                height={incomeH}
                rx="6"
                fill="url(#incomeGrad)"
                className="bar-rect"
              >
                <title>Income: {m.income.toFixed(2)}</title>
              </rect>
              <rect
                x={x + barWidth + 4}
                y={chartHeight - expenseH + 10}
                width={barWidth}
                height={expenseH}
                rx="6"
                fill="url(#expenseGrad)"
                className="bar-rect"
              >
                <title>Expenses: {m.expenses.toFixed(2)}</title>
              </rect>
              <text
                x={x + barWidth + 2}
                y={chartHeight + 30}
                textAnchor="middle"
                fill="#64748b"
                fontSize="11"
                fontWeight="600"
              >
                {MONTH_LABELS[i]}
              </text>
            </g>
          )
        })}

        <defs>
          <linearGradient id="incomeGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#22c55e" />
            <stop offset="100%" stopColor="#16a34a" />
          </linearGradient>
          <linearGradient id="expenseGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#ef4444" />
            <stop offset="100%" stopColor="#dc2626" />
          </linearGradient>
        </defs>
      </svg>
    </div>
  )
}

function PieChart({ data, title }) {
  const entries = Object.entries(data || {})
  const total = entries.reduce((s, [, v]) => s + v, 0)

  if (entries.length === 0 || total === 0) {
    return (
      <div className="pie-wrapper">
        <h3>{title}</h3>
        <p className="empty-chart">No data for this period</p>
      </div>
    )
  }

  const radius = 80
  const cx = 100
  const cy = 100
  let cumulative = 0

  const slices = entries.map(([label, value], i) => {
    const pct = value / total
    const startAngle = cumulative * 2 * Math.PI - Math.PI / 2
    cumulative += pct
    const endAngle = cumulative * 2 * Math.PI - Math.PI / 2
    const largeArc = pct > 0.5 ? 1 : 0

    const x1 = cx + radius * Math.cos(startAngle)
    const y1 = cy + radius * Math.sin(startAngle)
    const x2 = cx + radius * Math.cos(endAngle)
    const y2 = cy + radius * Math.sin(endAngle)

    const d =
      entries.length === 1
        ? `M ${cx} ${cy - radius} A ${radius} ${radius} 0 1 1 ${cx - 0.001} ${cy - radius} Z`
        : `M ${cx} ${cy} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2} Z`

    return (
      <path
        key={label}
        d={d}
        fill={PALETTE[i % PALETTE.length]}
        className="pie-slice"
      >
        <title>
          {label}: {value.toFixed(2)} ({(pct * 100).toFixed(1)}%)
        </title>
      </path>
    )
  })

  return (
    <div className="pie-wrapper">
      <h3>{title}</h3>
      <div className="pie-layout">
        <svg viewBox="0 0 200 200" width="200" height="200">
          {slices}
        </svg>
        <ul className="pie-legend">
          {entries.map(([label, value], i) => (
            <li key={label}>
              <span
                className="legend-dot"
                style={{ background: PALETTE[i % PALETTE.length] }}
              />
              <span className="legend-label">{label}</span>
              <strong>{value.toFixed(2)}</strong>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default function Dashboard({ entries }) {
  const currentYear = new Date().getFullYear()
  const [year, setYear] = useState(currentYear)
  const [summaryData, setSummaryData] = useState(null)
  const [loading, setLoading] = useState(true)

  const localSummary = useMemo(() => {
    const monthlyIncome = Array(12).fill(0)
    const monthlyExpense = Array(12).fill(0)
    const incByCat = {}
    const expByCat = {}

    entries
      .filter((e) => {
        const d = new Date(e.date)
        return d.getFullYear() === year
      })
      .forEach((e) => {
        const monthIdx = new Date(e.date).getMonth()
        const amt = Number(e.amount)
        if (e.type === 'INCOME') {
          monthlyIncome[monthIdx] += amt
          incByCat[e.category] = (incByCat[e.category] || 0) + amt
        } else {
          monthlyExpense[monthIdx] += amt
          expByCat[e.category] = (expByCat[e.category] || 0) + amt
        }
      })

    return {
      totalIncome: monthlyIncome.reduce((a, b) => a + b, 0),
      totalExpenses: monthlyExpense.reduce((a, b) => a + b, 0),
      balance:
        monthlyIncome.reduce((a, b) => a + b, 0) -
        monthlyExpense.reduce((a, b) => a + b, 0),
      months: monthlyIncome.map((inc, i) => ({
        month: `${year}-${String(i + 1).padStart(2, '0')}`,
        income: inc,
        expenses: monthlyExpense[i],
      })),
      incomeByCategory: incByCat,
      expenseByCategory: expByCat,
    }
  }, [entries, year])

  useEffect(() => {
    let cancelled = false
    setLoading(true)

    fetch(`http://localhost:8080/v1/income/summary?year=${year}`)
      .then((r) => {
        if (!r.ok) throw new Error('fail')
        return r.json()
      })
      .then((payload) => {
        if (!cancelled) {
          setSummaryData(payload.data)
          setLoading(false)
        }
      })
      .catch(() => {
        if (!cancelled) {
          setSummaryData(null)
          setLoading(false)
        }
      })

    return () => {
      cancelled = true
    }
  }, [year])

  const summary = summaryData || localSummary
  const months = summary.months || []

  const years = []
  for (let y = currentYear - 4; y <= currentYear + 1; y++) {
    years.push(y)
  }

  return (
    <section className="dashboard">
      <div className="dashboard-header">
        <div>
          <h2>Monthly Dashboard</h2>
          <p className="dashboard-sub">
            {year} &middot; Income vs Expenses overview
          </p>
        </div>
        <select
          className="year-select"
          value={year}
          onChange={(e) => setYear(Number(e.target.value))}
        >
          {years.map((y) => (
            <option key={y} value={y}>
              {y}
            </option>
          ))}
        </select>
      </div>

      <div className="kpi-row">
        <div className="kpi income-kpi">
          <span>Total Income</span>
          <strong>{summary.totalIncome.toFixed(2)}</strong>
        </div>
        <div className="kpi expense-kpi">
          <span>Total Expenses</span>
          <strong>{summary.totalExpenses.toFixed(2)}</strong>
        </div>
        <div className="kpi balance-kpi">
          <span>Net Balance</span>
          <strong
            className={summary.balance >= 0 ? 'positive' : 'negative'}
          >
            {summary.balance >= 0 ? '+' : ''}
            {summary.balance.toFixed(2)}
          </strong>
        </div>
      </div>

      {loading ? (
        <div className="chart-loading">Loading chart data…</div>
      ) : (
        <>
          <div className="chart-panel panel">
            <h3>Income vs Expenses by Month</h3>
            <div className="chart-legend-row">
              <span className="legend-item">
                <span className="dot income-dot" /> Income
              </span>
              <span className="legend-item">
                <span className="dot expense-dot" /> Expenses
              </span>
            </div>
            <BarChart months={months} />
          </div>

          <div className="pie-row">
            <div className="panel">
              <PieChart
                data={summary.incomeByCategory}
                title="Income by Category"
              />
            </div>
            <div className="panel">
              <PieChart
                data={summary.expenseByCategory}
                title="Expenses by Category"
              />
            </div>
          </div>
        </>
      )}
    </section>
  )
}
