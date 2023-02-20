import subprocess
from flask import Flask, render_template, request

app = Flask(__name__, template_folder=".")


@app.route("/", methods=["GET", "POST"])
def home():
    if request.method != "POST":
        return render_template("index.html")

    out = ""
    source_code = request.form["source-code"]
    if source_code:
        res = subprocess.run(["docker", "run", "python:3.8.2-alpine", "sh", "-c",
                              f"python -c '{source_code}'"], capture_output=True)
        out = (res.stderr if res.stderr else res.stdout).decode("utf-8")

    return render_template("index.html", source_code=source_code, out=out)
