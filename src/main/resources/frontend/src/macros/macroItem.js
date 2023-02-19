import Styles from "./macroItem.module.css";
import MacroEditor from "./macroEditor";
import fetch from "../fetch";

function MacroItem(props) {
    let editMacro = () => {
        let updateMacro = async macro => {
            try {
                await fetch.post(`/macros/${props.data.id}`, macro);
                await props.onUpdate();
            } catch (e) {
                alert("Could not save the macro.");
                throw e;
            }
        };
        MacroEditor.mount(<MacroEditor onSave={updateMacro} macro={props.data} />)
    }

    let deleteMacro = async () => {
        if (!window.confirm(`Do you want to delete ${props.data.name}? You won't be able to recover the macro.`)) return;
        try {
            await fetch.delete(`/macros/${props.data.id}`);
            await props.onUpdate();
        } catch (e) {
            alert("Could not delete the macro.");
            throw e;
        }
    }

    let playMacro = async () => {
        try {
            await fetch.post(`/macros/${props.data.id}/play`, {});
        } catch (e) {
            alert("Could not play the macro.");
            throw e;
        }
    }

    return <div className={Styles.macro}>
        <span>{props.data.name}</span>
        <div>
            <button onClick={playMacro}>Play</button>
            <button onClick={editMacro}>Edit</button>
            <button onClick={deleteMacro}>Remove</button>
        </div>
    </div>
}

export default MacroItem;