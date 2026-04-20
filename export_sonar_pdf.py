import requests
from fpdf import FPDF

# SonarQube server details
SONAR_URL = "http://localhost:9000"
PROJECT_KEY = "demo"
TOKEN = "squ_cc65d8552f187448d4408411b7665a0052b3e81f"  # replace with your SonarQube token

# Fetch vulnerabilities from SonarQube
def fetch_vulnerabilities():
    url = f"{SONAR_URL}/api/issues/search"
    params = {"componentKeys": PROJECT_KEY, "types": "VULNERABILITY"}
    resp = requests.get(url, params=params, auth=(TOKEN, ""))
    resp.raise_for_status()
    return resp.json()["issues"]

# Generate PDF report
def generate_pdf(issues):
    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=12)

    pdf.cell(200, 10, f"SonarQube Vulnerability Report - {PROJECT_KEY}", ln=True, align="C")
    pdf.ln(10)

    for issue in issues:
        pdf.multi_cell(0, 10,
            f"Type: {issue['type']}\n"
            f"Severity: {issue['severity']}\n"
            f"Message: {issue['message']}\n"
            f"File: {issue['component']}\n"
            f"SonarQube URL: {SONAR_URL}/project/issues?id={PROJECT_KEY}&open={issue['key']}\n"
            "------------------------------------------------------------"
        )
        pdf.ln(5)

    pdf.output("sonar_vulnerabilities.pdf")
    print("✅ PDF generated: sonar_vulnerabilities.pdf")
    print(f"🔗 Dashboard: {SONAR_URL}/dashboard?id={PROJECT_KEY}")

if __name__ == "__main__":
    issues = fetch_vulnerabilities()
    generate_pdf(issues)
