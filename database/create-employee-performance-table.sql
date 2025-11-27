-- Create employee_performance table
-- Compatible with PostgreSQL (primary) and MySQL

-- ============================================
-- FOR POSTGRESQL
-- ============================================

-- Create employee_performance table if it doesn't exist
CREATE TABLE IF NOT EXISTS employee_performance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,  -- Format: '2025-11'
    performance_score NUMERIC(5,2) CHECK (performance_score >= 0 AND performance_score <= 100),
    rating VARCHAR(20),  -- 'Excellent', 'Good', 'Average', 'Poor'
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE(employee_id, month)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_employee_performance_employee_id ON employee_performance(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_performance_month ON employee_performance(month);

-- Sample data (optional)
INSERT INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES 
    (1, '2025-11', 95.50, 'Excellent', 'Outstanding performance this month'),
    (2, '2025-11', 88.00, 'Good', 'Good work, keep it up'),
    (3, '2025-11', 75.50, 'Average', 'Room for improvement')
ON CONFLICT (employee_id, month) DO NOTHING;

-- Verify the table
SELECT * FROM employee_performance ORDER BY employee_id, month;

-- ============================================
-- FOR MYSQL (Alternative version)
-- ============================================
/*
CREATE TABLE IF NOT EXISTS employee_performance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,
    performance_score DECIMAL(5,2) CHECK (performance_score >= 0 AND performance_score <= 100),
    rating VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE KEY unique_employee_month (employee_id, month)
);

CREATE INDEX idx_employee_performance_employee_id ON employee_performance(employee_id);
CREATE INDEX idx_employee_performance_month ON employee_performance(month);

INSERT IGNORE INTO employee_performance (employee_id, month, performance_score, rating, notes) 
VALUES 
    (1, '2025-11', 95.50, 'Excellent', 'Outstanding performance this month'),
    (2, '2025-11', 88.00, 'Good', 'Good work, keep it up'),
    (3, '2025-11', 75.50, 'Average', 'Room for improvement');
*/




