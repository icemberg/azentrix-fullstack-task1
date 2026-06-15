import { useEffect, useMemo, useState } from 'react'
import './App.css'

const today = new Date().toISOString().slice(0, 10)

const baseForm = {
  type: 'INCOME',
  description: '',
  amount: '',
  category: '',
  date: today,
}

function App() {
  
  const [entries, setEntries] = useState(() => {
    const savedEntries = localStorage.getItem('budget-entries')
    return savedEntries ? JSON.parse(savedEntries) : []
  })
  
  const [form, setForm] = useState(baseForm)
  const [editingId, setEditingId] = useState(null)
  const [status, setStatus] = useState('Ready to track your money')

  useEffect(() => {
    localStorage.setItem('budget-entries', JSON.stringify(entries))
  }, [entries])

  const loadEntries = async () => {
    try {
      const response = await fetch('http://localhost:8080/v1/income')
      if (!response.ok) {
        throw new Error('Backend unavailable')
      }
      const payload = await response.json()
      const data = Array.isArray(payload.data) ? payload.data : []
      setEntries(data)
      setStatus('Synced with your tracker')
    } catch {
      // Safe local fallback read if API fails
      const savedEntries = localStorage.getItem('budget-entries')
      if (savedEntries) {
        setEntries(JSON.parse(savedEntries))
      }
      setStatus('Using local storage (Backend offline)')
    }
  }

  useEffect(() => {
    loadEntries()
  }, [])

  const summary = useMemo(() => {
    const income = entries
      .filter((entry) => entry.type === 'INCOME')
      .reduce((sum, entry) => sum + Number(entry.amount), 0)
    const expenses = entries
      .filter((entry) => entry.type === 'EXPENSE')
      .reduce((sum, entry) => sum + Number(entry.amount), 0)

    return {
      income,
      expenses,
      balance: income - expenses,
    }
  }, [entries])

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const resetForm = () => {
    setForm(baseForm)
    setEditingId(null)
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    const payload = {
      ...form,
      amount: Number(form.amount),
    }

    if (!payload.description || !payload.category || !payload.amount || !payload.date) {
      setStatus('Please complete every field')
      return
    }

    try {
      const method = editingId ? 'PUT' : 'POST'
      const url = editingId
        ? `http://localhost:8080/v1/income/${editingId}`
        : 'http://localhost:8080/v1/income'

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        throw new Error('Request failed')
      }

      const responseData = await response.json()
      const savedEntry = responseData.data ?? responseData
      const nextEntries = editingId
        ? entries.map((entry) => (entry.id === editingId ? savedEntry : entry))
        : [savedEntry, ...entries]

      setEntries(nextEntries)
      resetForm()
      setStatus(editingId ? 'Entry updated' : 'Entry saved')
    } catch {
      const fallbackEntry = {
        ...payload,
        id: editingId ?? Date.now(),
      }
      const nextEntries = editingId
        ? entries.map((entry) => (entry.id === editingId ? fallbackEntry : entry))
        : [fallbackEntry, ...entries]

      setEntries(nextEntries)
      resetForm()
      setStatus('Saved locally')
    }
  }

  const startEditing = (entry) => {
    setEditingId(entry.id)
    setForm({
      type: entry.type,
      description: entry.description,
      amount: entry.amount,
      category: entry.category,
      date: entry.date,
    })
  }

  const handleDelete = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/v1/income/${id}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        throw new Error('Delete failed')
      }
      setEntries((current) => current.filter((entry) => entry.id !== id))
      setStatus('Entry removed')
    } catch {
      setEntries((current) => current.filter((entry) => entry.id !== id))
      setStatus('Removed locally')
    }
  }

  return (
    <div className="app-shell">
      <header className="hero-card">
        <div>
          <p className="eyebrow">Personal Budget Tracker</p>
          <h1>Take control of your money in one place.</h1>
          <p className="hero-text">
            Track daily income and expenses with categories, dates, and a quick summary.
          </p>
        </div>
        <div className="summary-card">
          <div>
            <span>Income</span>
            <strong>{summary.income.toFixed(2)}</strong>
          </div>
          <div>
            <span>Expenses</span>
            <strong>{summary.expenses.toFixed(2)}</strong>
          </div>
          <div>
            <span>Balance</span>
            <strong>{summary.balance.toFixed(2)}</strong>
          </div>
        </div>
      </header>

      <main className="content-grid">
        <section className="panel">
          <div className="panel-heading">
            <h2>{editingId ? 'Edit entry' : 'Add entry'}</h2>
            <p>{status}</p>
          </div>
          <form onSubmit={handleSubmit} className="entry-form">
            <label>
              Type
              <select name="type" value={form.type} onChange={handleChange}>
                <option value="INCOME">Income</option>
                <option value="EXPENSE">Expense</option>
              </select>
            </label>
            <label>
              Description
              <input
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Salary, groceries, rent"
              />
            </label>
            <label>
              Amount
              <input
                name="amount"
                type="number"
                step="0.01"
                value={form.amount}
                onChange={handleChange}
                placeholder="0.00"
              />
            </label>
            <label>
              Category
              <input
                name="category"
                value={form.category}
                onChange={handleChange}
                placeholder="Work, Food, Travel"
              />
            </label>
            <label>
              Date
              <input name="date" type="date" value={form.date} onChange={handleChange} />
            </label>
            <div className="form-actions">
              <button type="submit">{editingId ? 'Update entry' : 'Save entry'}</button>
              {editingId ? <button type="button" className="secondary" onClick={resetForm}>Cancel</button> : null}
            </div>
          </form>
        </section>

        <section className="panel entries-panel">
          <div className="panel-heading">
            <h2>Recent entries</h2>
            <p>{entries.length} total</p>
          </div>
          <div className="entry-list">
            {entries.length === 0 ? (
              <div className="empty-state">No entries yet. Add your first transaction.</div>
            ) : (
              entries.map((entry) => (
                <article key={entry.id} className={`entry-card ${entry.type.toLowerCase()}`}>
                  <div>
                    <p className="entry-type">{entry.type}</p>
                    <h3>{entry.description}</h3>
                    <p>{entry.category} • {entry.date}</p>
                  </div>
                  <div className="entry-meta">
                    <strong>{Number(entry.amount).toFixed(2)}</strong>
                    <div className="entry-actions">
                      <button type="button" onClick={() => startEditing(entry)}>
                        Edit
                      </button>
                      <button type="button" className="danger" onClick={() => handleDelete(entry.id)}>
                        Delete
                      </button>
                    </div>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </main>
    </div>
  )
}

export default App
