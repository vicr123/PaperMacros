import Styles from "./macroList.module.css";
import {useEffect, useState} from "react";
import fetch from "../fetch";
import MacroItem from "./macroItem";
import MacroEditor from "./macroEditor";

function renderMacros() {
    return <div>

    </div>
}

function MacroList() {
    const [macros, setMacros] = useState(null);

    let updateMacros = async () => {
        setMacros(await fetch.get("/macros"));
    }

    useEffect(() => {
        updateMacros().then(x => x);
    }, []);

    let addEvent = () => {
        let saveNewMacro = async macro => {
            try {
                await fetch.post("/macros", macro);
                await updateMacros();
            } catch (e) {
                alert("Could not create the macro.");
                throw e;
            }
        };
        MacroEditor.mount(<MacroEditor onSave={saveNewMacro} />)
    };

    return <div className={Styles.macroListContainer}>
        <div className={Styles.macroList}>
            {macros?.map((macro, i) => <MacroItem key={i} data={macro} onUpdate={updateMacros} />)}
            <button className={Styles.newButton} onClick={addEvent}>New Macro</button>
        </div>
    </div>
}

export default MacroList;