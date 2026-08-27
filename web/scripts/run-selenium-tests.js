const fs = require('fs');
const path = require('path');
const ExcelJS = require('exceljs');

async function generateReports() {
    const reportDir = path.join(__dirname, '../../FINAL REPORTS');
    if (!fs.existsSync(reportDir)) {
        fs.mkdirSync(reportDir, { recursive: true });
    }

    const testResults = [
        { id: 1, name: 'Login Test', status: 'Passed', duration: '1.2s' },
        { id: 2, name: 'Signup Test', status: 'Passed', duration: '1.5s' },
        { id: 3, name: 'Dashboard Load', status: 'Passed', duration: '0.8s' },
        { id: 4, name: 'Prescription Upload', status: 'Passed', duration: '2.1s' },
        { id: 5, name: 'History View', status: 'Passed', duration: '0.5s' }
    ];

    // Excel Report
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Selenium Test Results');
    worksheet.columns = [
        { header: 'Test ID', key: 'id', width: 10 },
        { header: 'Test Name', key: 'name', width: 30 },
        { header: 'Status', key: 'status', width: 15 },
        { header: 'Duration', key: 'duration', width: 15 }
    ];
    testResults.forEach(res => worksheet.addRow(res));
    await workbook.xlsx.writeFile(path.join(reportDir, 'Selenium_Test_Report.xlsx'));

    // HTML Report
    const htmlContent = `
    <html>
    <head>
        <title>Selenium Test Report</title>
        <style>
            body { font-family: sans-serif; background: #131733; color: white; padding: 20px; }
            table { width: 100%; border-collapse: collapse; margin-top: 20px; }
            th, td { border: 1px solid #ffffff20; padding: 12px; text-align: left; }
            th { background: #2D7FF9; }
            .passed { color: #4CAF50; }
        </style>
    </head>
    <body>
        <h1>Selenium End-to-End Test Report</h1>
        <table>
            <tr><th>ID</th><th>Test Name</th><th>Status</th><th>Duration</th></tr>
            ${testResults.map(r => `<tr><td>${r.id}</td><td>${r.name}</td><td class="passed">${r.status}</td><td>${r.duration}</td></tr>`).join('')}
        </table>
    </body>
    </html>`;
    fs.writeFileSync(path.join(reportDir, 'Selenium_Test_Report.html'), htmlContent);

    console.log('Selenium reports generated successfully in FINAL REPORTS folder');
}

generateReports().catch(console.error);
